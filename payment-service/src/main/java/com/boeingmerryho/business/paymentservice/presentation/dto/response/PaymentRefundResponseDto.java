package com.boeingmerryho.business.paymentservice.presentation.dto.response;

public record PaymentRefundResponseDto(
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
