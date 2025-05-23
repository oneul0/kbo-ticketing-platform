package com.boeingmerryho.business.paymentservice.presentation.dto.response;

public record PaymentMembershipRefundResponseDto(
	Long id,
	Long userId,
	Long paymentId,
	Integer price,
	Integer discountPrice,
	String method,
	String discountType,
	String discountAmount
) {
}
