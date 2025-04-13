package com.boeingmerryho.business.paymentservice.infrastructure;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayCancelRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayCancelResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyResponse;
import com.boeingmerryho.business.paymentservice.infrastructure.feignclient.KakaoFeignClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

	private final KakaoFeignClient kakaoFeignClient;

	public KakaoPayReadyResponse callReady(KakaoPayReadyRequest request, String secretKey, String authPrefix) {
		return kakaoFeignClient.ready(
			authPrefix + secretKey,
			MediaType.APPLICATION_JSON_VALUE,
			request
		);
	}

	public KakaoPayApproveResponse callApprove(KakaoPayApproveRequest request, String secretKey, String authPrefix) {
		return kakaoFeignClient.approve(
			authPrefix + secretKey,
			MediaType.APPLICATION_JSON_VALUE,
			request
		);
	}

	public KakaoPayCancelResponse callCancel(KakaoPayCancelRequest request, String secretKey, String authPrefix) {
		return kakaoFeignClient.cancel(
			authPrefix + secretKey,
			MediaType.APPLICATION_JSON_VALUE,
			request
		);
	}
}
