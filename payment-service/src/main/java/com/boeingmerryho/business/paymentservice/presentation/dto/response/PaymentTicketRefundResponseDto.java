package com.boeingmerryho.business.paymentservice.presentation.dto.response;

import java.time.LocalDateTime;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.Amount;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.ApprovedCancelAmount;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.CancelAvailableAmount;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.CanceledAmount;

public record PaymentTicketRefundResponseDto(
	Long paymentId,
	String aid,
	String tid,
	String cid,
	String status,
	String partnerOrderId,
	String partnerUserId,
	String paymentMethodType,
	Amount amount,
	ApprovedCancelAmount approvedCancelAmount,
	CanceledAmount canceledAmount,
	CancelAvailableAmount cancelAvailableAmount,
	String itemName,
	String itemCode,
	Integer quantity,
	LocalDateTime createdAt,
	LocalDateTime approvedAt,
	LocalDateTime canceledAt
) {
}
