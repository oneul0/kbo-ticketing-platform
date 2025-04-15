package com.boeingmerryho.business.paymentservice.infrastructure.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.PaymentService;
import com.boeingmerryho.business.paymentservice.application.PaymentStrategy;
import com.boeingmerryho.business.paymentservice.application.PaymentStrategyFactory;
import com.boeingmerryho.business.paymentservice.application.dto.PaymentApplicationMapper;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentTicketCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentDetailRepository;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.domain.type.DiscountType;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentStatus;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.infrastructure.KafkaProducerHelper;
import com.boeingmerryho.business.paymentservice.infrastructure.MembershipApiClient;
import com.boeingmerryho.business.paymentservice.infrastructure.PaySessionHelper;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.ErrorCode;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaySessionHelper paySessionHelper;
	private final PaymentRepository paymentRepository;
	private final PaymentStrategyFactory strategyFactory;
	private final MembershipApiClient membershipApiClient;
	private final KafkaProducerHelper kafkaProducerHelper;
	private final PaymentDetailRepository paymentDetailRepository;
	private final PaymentApplicationMapper paymentApplicationMapper;

	@Override
	@Transactional
	public PaymentReadyResponseServiceDto pay(
		PaymentReadyRequestServiceDto requestServiceDto
	) {
		// assertInExpiredTimePayment(requestServiceDto.paymentId());
		Payment payment = paymentRepository.findById(requestServiceDto.paymentId())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		Double discount = membershipApiClient.getDiscount(requestServiceDto.userId())
			.orElseThrow(() -> new PaymentException(ErrorCode.MEMBERSHIP_SERVICE_UNAVAILABLE));
		payment.updateDiscountInfo(
			discount,
			DiscountType.from(requestServiceDto.discountType())
		);
		PaymentStrategy strategy = strategyFactory.getStrategy(requestServiceDto.method());
		return strategy.pay(payment, requestServiceDto);
	}

	@Override
	@Transactional
	public PaymentApproveResponseServiceDto approvePayment(
		PaymentApproveRequestServiceDto requestServiceDto
	) {
		PaymentSession paymentSession = paySessionHelper.getPaymentInfo(String.valueOf(requestServiceDto.paymentId()))
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_INFO_NOT_FOUND));
		Payment payment = paymentRepository.findById(requestServiceDto.paymentId())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		PaymentStrategy strategy = strategyFactory.getStrategy(paymentSession.method());
		PaymentApproveResponseServiceDto responseServiceDto = strategy.approve(
			paymentSession,
			payment,
			requestServiceDto
		);
		paySessionHelper.deletePaymentExpiredTime(String.valueOf(payment.getId()));
		return responseServiceDto;
	}

	@Override
	@Transactional
	public PaymentTicketCancelResponseServiceDto cancelTicketPayment(
		PaymentTicketCancelRequestServiceDto requestServiceDto) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentTicketCancelResponseServiceDto(payment.getId());
	}

	@Override
	@Transactional
	public PaymentMembershipCancelResponseServiceDto cancelMembershipPayment(
		PaymentMembershipCancelRequestServiceDto requestServiceDto) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentMembershipCancelResponseServiceDto(payment.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentDetailResponseServiceDto getPaymentDetail(
		PaymentDetailRequestServiceDto requestServiceDto) {
		PaymentDetail paymentDetail = paymentDetailRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));
		return paymentApplicationMapper.toPaymentDetailResponseServiceDto(paymentDetail);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentDetailResponseServiceDto> searchPaymentDetail(
		PaymentDetailSearchRequestServiceDto requestServiceDto) {
		Page<PaymentDetail> paymentDetails = paymentRepository.searchPaymentDetail(
			createSearchContext(requestServiceDto)
		);
		return paymentDetails.map(paymentApplicationMapper::toPaymentDetailResponseServiceDto);
	}

	private void assertInExpiredTimePayment(Long paymentId) {
		LocalDateTime expiredTime = paySessionHelper.getPaymentExpiredTime(paymentId.toString())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		if (!LocalDateTime.now().isBefore(expiredTime)) {
			paySessionHelper.deletePaymentExpiredTime(paymentId.toString());
			throw new PaymentException(ErrorCode.PAYMENT_EXPIRED);
		}
	}

	private void assertCancellablePayment(Payment payment) {
		if (!payment.validateStatus(PaymentStatus.CONFIRMED)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
		if (!payment.validateType(PaymentType.TICKET)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
	}

	private PaymentDetailSearchContext createSearchContext(
		PaymentDetailSearchRequestServiceDto requestServiceDto) {
		return PaymentDetailSearchContext.builder()
			.id(requestServiceDto.id())
			.paymentId(requestServiceDto.paymentId())
			.customPageable(requestServiceDto.customPageable())
			.isDeleted(requestServiceDto.isDeleted())
			.build();
	}
}
