package com.boeingmerryho.business.paymentservice.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayCancelRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;

@Component
public class KakaoPayRequestFactory {

	@Value("${kakaopay.cid}")
	String cid;

	@Value("${kakaopay.redirect-url}")
	String redirectUrl;

	private final String APPROVE_PATH = "/api/v1/payments/approve";
	private final String CANCEL_PATH = "/api/v1/payments/cancel";
	private final String FAIL_PATH = "/kakao/payments/ready/fail";

	public KakaoPayReadyRequest createReadyRequest(
		Payment payment,
		PaymentReadyRequestServiceDto requestServiceDto
	) {
		return KakaoPayReadyRequest.builder()
			.cid(cid)
			.partnerOrderId(requestServiceDto.paymentId().toString())
			.partnerUserId(requestServiceDto.userId().toString())
			.itemName(requestServiceDto.type())
			.quantity(requestServiceDto.tickets() == null ? 1 : requestServiceDto.tickets().size())
			.totalAmount(payment.getDiscountPrice())
			.vatAmount(0)
			.taxFreeAmount(0)
			.approvalUrl(redirectUrl + APPROVE_PATH)
			.cancelUrl(redirectUrl + CANCEL_PATH)
			.failUrl(redirectUrl + FAIL_PATH)
			.build();
	}

	public KakaoPayApproveRequest createApproveRequest(
		PaymentSession paymentSession,
		PaymentApproveRequestServiceDto requestServiceDto
	) {
		return KakaoPayApproveRequest.builder()
			.cid(paymentSession.cid())
			.tid(paymentSession.tid())
			.partnerOrderId(paymentSession.partnerOrderId())
			.partnerUserId(paymentSession.partnerUserId())
			.pgToken(requestServiceDto.pgToken())
			.build();
	}

	public KakaoPayCancelRequest createCancelRequest(
		PaymentDetail paymentDetail
	) {
		return new KakaoPayCancelRequest(
			paymentDetail.getKakaoPayInfo().getCid(),
			paymentDetail.getKakaoPayInfo().getTid(),
			paymentDetail.getDiscountPrice(),
			0
		);
	}
}
