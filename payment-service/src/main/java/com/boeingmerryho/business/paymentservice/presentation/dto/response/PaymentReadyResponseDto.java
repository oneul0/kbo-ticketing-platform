package com.boeingmerryho.business.paymentservice.presentation.dto.response;

import java.time.LocalDateTime;

public record PaymentReadyResponseDto(
	String tid,
	String nextRedirectAppUrl,
	String nextRedirectMobileUrl,
	String nextRedirectPcUrl,
	String androidAppScheme,
	String iosAppScheme,
	LocalDateTime createdAt
) {
}
