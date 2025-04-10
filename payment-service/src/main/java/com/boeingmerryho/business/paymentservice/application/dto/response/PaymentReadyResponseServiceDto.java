package com.boeingmerryho.business.paymentservice.application.dto.response;

import java.time.LocalDateTime;

public record PaymentReadyResponseServiceDto(
	String tid,
	String nextRedirectAppUrl,
	String nextRedirectMobileUrl,
	String nextRedirectPcUrl,
	String androidAppScheme,
	String iosAppScheme,
	LocalDateTime created_at
) {
}
