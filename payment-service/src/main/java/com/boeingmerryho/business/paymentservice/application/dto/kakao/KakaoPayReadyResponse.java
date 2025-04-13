package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoPayReadyResponse(
	@JsonProperty("tid") String tid,
	@JsonProperty("next_redirect_app_url") String nextRedirectAppUrl,
	@JsonProperty("next_redirect_mobile_url") String nextRedirectMobileUrl,
	@JsonProperty("next_redirect_pc_url") String nextRedirectPcUrl,
	@JsonProperty("android_app_scheme") String androidAppScheme,
	@JsonProperty("ios_app_scheme") String iosAppScheme,
	@JsonProperty("created_at") LocalDateTime createdAt
) {
}
