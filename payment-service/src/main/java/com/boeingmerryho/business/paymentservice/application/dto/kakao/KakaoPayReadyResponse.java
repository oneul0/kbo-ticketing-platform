package com.boeingmerryho.business.paymentservice.application.dto.kakao;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoPayReadyResponse {
	@JsonProperty("tid")
	private String tid;

	@JsonProperty("next_redirect_app_url")
	private String nextRedirectAppUrl;

	@JsonProperty("next_redirect_mobile_url")
	private String nextRedirectMobileUrl;

	@JsonProperty("next_redirect_pc_url")
	private String nextRedirectPcUrl;

	@JsonProperty("android_app_scheme")
	private String androidAppScheme;

	@JsonProperty("ios_app_scheme")
	private String iosAppScheme;

	@JsonProperty("created_at")
	private LocalDateTime createdAt;
}
