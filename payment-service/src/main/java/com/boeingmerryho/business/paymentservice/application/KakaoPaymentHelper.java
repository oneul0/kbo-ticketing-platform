package com.boeingmerryho.business.paymentservice.application;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPaymentSession;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoPaymentHelper {

	private final KakaoPaymentSessionService kakaoPaymentSessionService;

	public void savePaymentInfo(String paymentId, KakaoPaymentSession session) {
		kakaoPaymentSessionService.saveSession(paymentId, session);
	}

	public Optional<KakaoPaymentSession> getPaymentInfo(String paymentId) {
		return kakaoPaymentSessionService.getSession(paymentId);
	}

	public void deletePaymentInfo(String paymentId) {
		kakaoPaymentSessionService.deleteSession(paymentId);
	}
}
