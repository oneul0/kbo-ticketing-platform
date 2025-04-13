package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import java.util.List;

import lombok.Builder;

@Builder
public record KakaoPaymentSession(
	String tid,
	String cid,
	String partnerOrderId,
	String partnerUserId,
	List<String> tickets,
	Integer totalAmount,
	Integer quantity,
	String itemName,
	String createdAt
) {
}
