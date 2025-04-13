package com.boeingmerryho.business.paymentservice.application.dto.response;

public record PaymentApproveResponseServiceDto(
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
