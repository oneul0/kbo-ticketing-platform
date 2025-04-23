package com.boeingmerryho.business.paymentservice.application.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;

@Component
public class PaymentSessionFactory {

	@Value("${kakaopay.cid}")
	String cid;

	public PaymentSession createSession(
		Payment payment,
		KakaoPayReadyRequest request,
		KakaoPayReadyResponse response,
		PaymentReadyRequestServiceDto requestServiceDto
	) {
		if (payment.getType() == PaymentType.TICKET) {
			return PaymentSession.builder()
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
		}
		if (payment.getType() == PaymentType.MEMBERSHIP) {
			return PaymentSession.builder()
				.cid(cid)
				.tid(response.tid())
				.partnerOrderId(request.partnerOrderId())
				.partnerUserId(request.partnerUserId())
				.membershipId(requestServiceDto.membershipId())
				.totalAmount(payment.getDiscountPrice())
				.quantity(1)
				.itemName(requestServiceDto.type())
				.createdAt(response.createdAt().toString())
				.method(requestServiceDto.method())
				.build();
		}
		return null;
	}
}
