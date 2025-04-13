package com.boeingmerryho.business.paymentservice.infrastructure;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.PaymentStrategy;
import com.boeingmerryho.business.paymentservice.application.dto.PaymentApplicationMapper;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.domain.entity.KakaoPayInfo;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.repository.PaymentDetailRepository;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoPayStrategy implements PaymentStrategy {

	@Value("${kakaopay.cid}")
	String cid;

	@Value("${kakaopay.secret-key}")
	String secretKey;

	@Value("${kakaopay.auth-prefix}")
	String authPrefix;

	@Value("${kakaopay.redirect-url}")
	String redirectUrl;

	private final String APPROVE_PATH = "/api/v1/payments/approve";
	private final String CANCEL_PATH = "/api/v1/payments/cancel";
	private final String FAIL_PATH = "/kakao/payments/ready/fail";

	private final KakaoApiClient kakaoApiClient;
	private final PaySessionHelper paySessionHelper;
	private final PaymentDetailRepository paymentDetailRepository;
	private final PaymentApplicationMapper paymentApplicationMapper;

	@Override
	public PaymentReadyResponseServiceDto pay(
		Payment payment,
		PaymentReadyRequestServiceDto requestServiceDto
	) {
		KakaoPayReadyRequest request = KakaoPayReadyRequest.builder()
			.cid(cid)
			.partnerOrderId(requestServiceDto.paymentId() + UUID.randomUUID().toString())
			.partnerUserId(requestServiceDto.userId().toString())
			.itemName(requestServiceDto.type())
			.quantity(requestServiceDto.tickets().size())
			.totalAmount(payment.getDiscountPrice())
			.vatAmount(0)
			.taxFreeAmount(0)
			.approvalUrl(redirectUrl + APPROVE_PATH)
			.cancelUrl(redirectUrl + CANCEL_PATH)
			.failUrl(redirectUrl + FAIL_PATH)
			.build();

		KakaoPayReadyResponse response = kakaoApiClient.callReady(request, secretKey, authPrefix);
		PaymentSession session = PaymentSession.builder()
			.cid(cid)
			.tid(response.tid())
			.partnerOrderId(request.partnerOrderId())
			.partnerUserId(request.partnerUserId())
			.tickets(requestServiceDto.tickets())
			.totalAmount(payment.getDiscountPrice())
			.quantity(requestServiceDto.tickets().size())
			.itemName(requestServiceDto.type())
			.createdAt(response.createdAt().toString())
			.method(requestServiceDto.method())
			.build();

		paySessionHelper.savePaymentInfo(String.valueOf(requestServiceDto.paymentId()), session);

		log.info("[Payment Ready] tid: {}, nextRedirectPcUrl: {}, createdAt: {}",
			response.tid(),
			response.nextRedirectPcUrl(),
			response.createdAt()
		);

		return paymentApplicationMapper.toPaymentReadyResponseServiceDto(
			requestServiceDto.paymentId(),
			null,
			null,
			null,
			null);
	}

	@Override
	public PaymentApproveResponseServiceDto approve(
		PaymentSession paymentSession,
		Payment payment,
		PaymentApproveRequestServiceDto requestServiceDto
	) {
		KakaoPayApproveRequest request = KakaoPayApproveRequest.builder()
			.cid(paymentSession.cid())
			.tid(paymentSession.tid())
			.partnerOrderId(paymentSession.partnerOrderId())
			.partnerUserId(paymentSession.partnerUserId())
			.pgToken(requestServiceDto.pgToken())
			.build();
		KakaoPayApproveResponse response = kakaoApiClient.callApprove(request, secretKey, authPrefix);

		PaymentDetail paymentDetail = paymentDetailRepository.save(
			PaymentDetail.builder()
				.kakaoPayInfo(
					KakaoPayInfo.builder()
						.cid(paymentSession.cid())
						.tid(paymentSession.tid())
						.build()
				)
				.payment(payment)
				.discountPrice(payment.getDiscountPrice() - response.amount().discount())
				.method(PaymentMethod.KAKAOPAY)
				.discountAmount(payment.getTotalPrice() - payment.getDiscountPrice() - response.amount().discount())
				.build()
		);

		payment.confirmPayment();
		return paymentApplicationMapper.toPaymentApproveResponseServiceDto(paymentDetail);
	}

	@Override
	public PaymentMethod getSupportedMethod() {
		return PaymentMethod.KAKAOPAY;
	}
}
