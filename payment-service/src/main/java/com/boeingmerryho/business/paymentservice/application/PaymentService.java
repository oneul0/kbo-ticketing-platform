package com.boeingmerryho.business.paymentservice.application;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPaymentSession;
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
import com.boeingmerryho.business.paymentservice.domain.entity.KakaoPayInfo;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentDetailRepository;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentStatus;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.exception.ErrorCode;
import com.boeingmerryho.business.paymentservice.exception.PaymentException;
import com.boeingmerryho.business.paymentservice.infrastructure.KakaoFeignService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	@Value("${kakaopay.cid}")
	String cid;

	@Value("${kakaopay.secret-key}")
	String secretKey;

	@Value("${kakaopay.auth-prefix}")
	String authPrefix;

	@Value("${kakaopay.redirect-url}")
	String redirectUrl;

	private final PaymentRepository paymentRepository;
	private final PaymentDetailRepository paymentDetailRepository;
	private final KakaoFeignService kakaoFeignService;
	private final PaymentApplicationMapper paymentApplicationMapper;
	private final KakaoPaymentHelper kakaoPaymentHelper;

	@Transactional(readOnly = true)
	public PaymentReadyResponseServiceDto pay(PaymentReadyRequestServiceDto requestServiceDto) {

		Payment payment = paymentRepository.findById(requestServiceDto.paymentId())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

		KakaoPayReadyRequest request = KakaoPayReadyRequest.builder()
			.cid(cid)
			.partnerOrderId(payment.getId() + UUID.randomUUID().toString())
			.partnerUserId(requestServiceDto.userId().toString())
			.itemName(requestServiceDto.type())
			.quantity(requestServiceDto.tickets().size())
			.totalAmount(requestServiceDto.price())
			.taxFreeAmount(0)
			.approvalUrl(redirectUrl + "/api/v1/payments/approve")
			.cancelUrl(redirectUrl + "/kakao/payments/ready/cancel")
			.failUrl(redirectUrl + "/kakao/payments/ready/fail")
			.build();

		KakaoPayReadyResponse response = kakaoFeignService.ready(
			authPrefix + secretKey,
			"application/json",
			request);

		kakaoPaymentHelper.savePaymentInfo(
			String.valueOf(requestServiceDto.paymentId()),
			KakaoPaymentSession.builder()
				.cid(cid)
				.tid(response.getTid())
				.partnerOrderId(request.getPartnerOrderId())
				.partnerUserId(request.getPartnerUserId())
				.tickets(requestServiceDto.tickets())
				.totalAmount(requestServiceDto.price())
				.quantity(requestServiceDto.tickets().size())
				.itemName(requestServiceDto.type())
				.createdAt(response.getCreatedAt().toString())
				.build()
		);

		return paymentApplicationMapper.toPaymentReadyResponseServiceDto(response);
	}

	@Transactional
	public PaymentApproveResponseServiceDto approvePayment(PaymentApproveRequestServiceDto requestServiceDto) {
		KakaoPaymentSession paymentSession = kakaoPaymentHelper.getPaymentInfo(
				String.valueOf(requestServiceDto.paymentId()))
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

		KakaoPayApproveRequest request = KakaoPayApproveRequest.builder()
			.cid(paymentSession.getCid())
			.tid(paymentSession.getTid())
			.partnerOrderId(paymentSession.getPartnerOrderId())
			.partnerUserId(paymentSession.getPartnerUserId())
			.pgToken(requestServiceDto.pgToken())
			.build();

		KakaoPayApproveResponse response = kakaoFeignService.approve(
			authPrefix + secretKey,
			"application/json",
			request
		);

		Payment payment = paymentRepository.findById(requestServiceDto.paymentId())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

		// kakaopay logic
		paymentDetailRepository.save(
			PaymentDetail.builder()
				.kakaoPayInfo(
					KakaoPayInfo.builder()
						.cid(paymentSession.getCid())
						.tid(paymentSession.getTid())
						.build()
				)
				.payment(payment)
				.discountPrice(payment.getDiscountPrice() - response.getAmount().getDiscount())
				.method(PaymentMethod.KAKAOPAY)
				.discountAmount(
					payment.getTotalPrice() - payment.getDiscountPrice() - response.getAmount().getDiscount())
				.build()
		);

		payment.confirmPayment();

		return paymentApplicationMapper.toPaymentApproveResponseServiceDto(response);
	}

	@Transactional
	public PaymentTicketCancelResponseServiceDto cancelTicketPayment(
		PaymentTicketCancelRequestServiceDto requestServiceDto) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentTicketCancelResponseServiceDto(payment.getId());
	}

	private static void assertCancellablePayment(Payment payment) {
		if (!payment.validateStatus(PaymentStatus.CONFIRMED)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
		if (!payment.validateType(PaymentType.TICKET)) {
			throw new PaymentException(ErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
	}

	@Transactional
	public PaymentMembershipCancelResponseServiceDto cancelMembershipPayment(
		PaymentMembershipCancelRequestServiceDto requestServiceDto) {
		Payment payment = paymentRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		assertCancellablePayment(payment);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentMembershipCancelResponseServiceDto(payment.getId());
	}

	@Transactional(readOnly = true)
	public PaymentDetailResponseServiceDto getPaymentDetail(
		PaymentDetailRequestServiceDto requestServiceDto) {
		PaymentDetail paymentDetail = paymentDetailRepository.findById(requestServiceDto.id())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_DETAIL_NOT_FOUND));
		return paymentApplicationMapper.toPaymentDetailResponseServiceDto(paymentDetail);
	}

	@Transactional(readOnly = true)
	public Page<PaymentDetailResponseServiceDto> searchPaymentDetail(
		PaymentDetailSearchRequestServiceDto requestServiceDto) {
		Page<PaymentDetail> paymentDetails = paymentRepository.searchPaymentDetail(
			createSearchContext(requestServiceDto)
		);
		return paymentDetails.map(paymentApplicationMapper::toPaymentDetailResponseServiceDto);
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
