package com.boeingmerryho.business.paymentservice.presentation.dto.response;

import java.time.LocalDateTime;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.Amount;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.CardInfo;

public record PaymentApproveResponseDto(
	String partnerOrderId,
	String partnerUserId,
	String paymentMethodType,
	Amount amount,
	CardInfo cardInfo,
	String itemName,
	String itemCode,
	Integer quantity,
	LocalDateTime createdAt,
	LocalDateTime approvedAt
) {
}
