package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import java.util.List;

import com.boeingmerryho.business.paymentservice.presentation.dto.request.Ticket;

import lombok.Builder;

@Builder
public record PaymentSession(
	String tid,
	String cid,
	String partnerOrderId,
	String partnerUserId,
	List<Ticket> tickets,
	Long membershipId,
	Integer totalAmount,
	Integer quantity,
	String itemName,
	String createdAt,
	String method
) {
}
