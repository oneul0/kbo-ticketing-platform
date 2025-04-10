package com.boeingmerryho.business.paymentservice.infrastructure;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayApproveResponse;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyRequest;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPayReadyResponse;

@Component
@FeignClient(name = "kakaopay-service", url = "${kakaopay.url}")
public interface KakaoFeignService {

	@PostMapping(
		value = "/online/v1/payment/ready",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	KakaoPayReadyResponse ready(
		@RequestHeader("Authorization") String authorization,
		@RequestHeader("Content-Type") String contentType,
		@RequestBody KakaoPayReadyRequest request
	);

	@PostMapping(
		value = "/online/v1/payment/approve",
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	KakaoPayApproveResponse approve(
		@RequestHeader("Authorization") String authorization,
		@RequestHeader("Content-Type") String contentType,
		@RequestBody KakaoPayApproveRequest request
	);
}