package com.boeingmerryho.business.paymentservice.presentation.dto.response;

public record PaymentDetailResponseDto(
	Long id,
	Long userId,
	Long paymentId,
	Integer price,
	Integer discountPrice,
	String method,
	String discountType,
	String discountAmount,
	String accountNumber
) {
}
