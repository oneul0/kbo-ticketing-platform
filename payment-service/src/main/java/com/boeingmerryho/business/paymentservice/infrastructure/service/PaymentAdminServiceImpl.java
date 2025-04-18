package com.boeingmerryho.business.paymentservice.infrastructure.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.PaymentAdminService;
import com.boeingmerryho.business.paymentservice.application.PaymentStrategy;
import com.boeingmerryho.business.paymentservice.application.PaymentStrategyFactory;
import com.boeingmerryho.business.paymentservice.application.dto.PaymentApplicationMapper;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayCancelRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayCancelResponse;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveAdminRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipRefundRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentTicketCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentTicketRefundRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailAdminResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipRefundResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketRefundResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentMembership;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentTicket;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentDetailRepository;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentStatus;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.infrastructure.KakaoApiClient;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.ErrorCode;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentAdminServiceImpl implements PaymentAdminService {

	@Value("${kakaopay.secret-key}")
	String secretKey;

	@Value("${kakaopay.auth-prefix}")
	String authPrefix;

	private final KakaoApiClient kakaoApiClient;
	private final PaymentRepository paymentRepository;
	private final PaymentStrategyFactory strategyFactory;
	private final PaymentDetailRepository paymentDetailRepository;
	private final PaymentApplicationMapper paymentApplicationMapper;

	@Override
	@Transactional
	public PaymentTicketCancelResponseServiceDto cancelTicketPayment(
		PaymentTicketCancelRequestServiceDto requestServiceDto
	) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentTicketCancelResponseServiceDto(payment.getId());
	}

	private void assertCancellablePayment(
		Payment payment
	) {
		if (!payment.validateStatus(PaymentStatus.CONFIRMED)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
		if (!payment.validateType(PaymentType.TICKET)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
	}

	@Override
	@Transactional
	public PaymentMembershipCancelResponseServiceDto cancelMembershipPayment(
		PaymentMembershipCancelRequestServiceDto requestServiceDto
	) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentMembershipCancelResponseServiceDto(payment.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentDetailAdminResponseServiceDto getPaymentDetail(
		PaymentDetailRequestServiceDto requestServiceDto
	) {
		PaymentDetail paymentDetail = paymentDetailRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));

		return paymentApplicationMapper.toPaymentDetailAdminResponseServiceDto(paymentDetail);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentDetailAdminResponseServiceDto> searchPaymentDetail(
		PaymentDetailSearchRequestServiceDto requestServiceDto
	) {
		Page<PaymentDetail> paymentDetails = paymentRepository.searchPaymentDetail(
			createSearchContext(requestServiceDto)
		);
		return paymentDetails.map(paymentApplicationMapper::toPaymentDetailAdminResponseServiceDto);
	}

	private PaymentDetailSearchContext createSearchContext(
		PaymentDetailSearchRequestServiceDto requestServiceDto
	) {
		return PaymentDetailSearchContext.builder()
			.id(requestServiceDto.id())
			.paymentId(requestServiceDto.paymentId())
			.customPageable(requestServiceDto.customPageable())
			.isDeleted(requestServiceDto.isDeleted())
			.build();
	}

	@Override
	@Transactional
	public PaymentTicketRefundResponseServiceDto refundTicketPayment(
		PaymentTicketRefundRequestServiceDto requestServiceDto
	) {

		PaymentTicket paymentTicket = paymentRepository.findByPaymentTicketId(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_TICKET_NOT_FOUND));

		Payment payment = paymentTicket.getPayment();

		if (payment.validateStatus(PaymentStatus.REFUNDED)) {    // 이미 환불된 결제 건
			throw new PaymentException(ErrorCode.PAYMENT_ALREADY_REFUNDED);
		}

		if (!payment.validateStatus(PaymentStatus.REFUND_REQUESTED)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_UNAVAILABLE);
		}

		PaymentDetail paymentDetail = paymentDetailRepository.findPaymentDetailByPaymentId(
				payment.getId())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));

		// TODO 가상 계좌 환불
		// TODO 전략패턴

		KakaoPayCancelRequest request = new KakaoPayCancelRequest(
			paymentDetail.getKakaoPayInfo().getCid(),
			paymentDetail.getKakaoPayInfo().getTid(),
			paymentDetail.getDiscountPrice(),
			0
		);

		KakaoPayCancelResponse response = kakaoApiClient.callCancel(request, secretKey, authPrefix);

		paymentDetail.getPayment().refundPayment();

		return paymentApplicationMapper.toPaymentTicketRefundResponseServiceDto(
			paymentDetail.getPayment().getId(),
			response
		);
	}

	@Override
	@Transactional
	public PaymentMembershipRefundResponseServiceDto refundMembershipPayment(
		PaymentMembershipRefundRequestServiceDto requestServiceDto
	) {

		PaymentMembership paymentMembership = paymentRepository.findByPaymentMembershipId(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_TICKET_NOT_FOUND));

		Payment payment = paymentMembership.getPayment();

		if (payment.validateStatus(PaymentStatus.REFUNDED)) {
			throw new PaymentException(ErrorCode.PAYMENT_ALREADY_REFUNDED);
		}

		if (!payment.validateStatus(PaymentStatus.REFUND_REQUESTED)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_UNAVAILABLE);
		}

		PaymentDetail paymentDetail = paymentDetailRepository.findPaymentDetailByPaymentId(
				payment.getId())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));

		// TODO 가상 계좌 환불
		// TODO 전략패턴

		KakaoPayCancelRequest request = new KakaoPayCancelRequest(
			paymentDetail.getKakaoPayInfo().getCid(),
			paymentDetail.getKakaoPayInfo().getTid(),
			paymentDetail.getDiscountPrice(),
			0
		);

		KakaoPayCancelResponse response = kakaoApiClient.callCancel(request, secretKey, authPrefix);

		paymentDetail.getPayment().refundPayment();

		return paymentApplicationMapper.toPaymentMembershipRefundResponseServiceDto(
			paymentDetail.getPayment().getId(),
			response
		);

	}

	@Override
	@Transactional
	public PaymentApproveResponseServiceDto approvePayment(
		PaymentApproveAdminRequestServiceDto requestServiceDto
	) {
		Payment payment = paymentRepository.findById(requestServiceDto.paymentId())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		PaymentStrategy strategy = strategyFactory.getStrategy(PaymentMethod.BANK_TRANSFER);
		return strategy.approve(payment, requestServiceDto);
	}
}
