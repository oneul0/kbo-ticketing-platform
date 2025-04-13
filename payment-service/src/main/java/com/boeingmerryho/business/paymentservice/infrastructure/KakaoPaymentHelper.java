package com.boeingmerryho.business.paymentservice.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.KakaoPaySessionService;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPaymentSession;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoPaymentHelper {

	private final KakaoPaySessionService kakaoPaySessionService;

	public void savePaymentInfo(String paymentId, KakaoPaymentSession session) {
		kakaoPaySessionService.saveSession(paymentId, session);
	}

	public Optional<KakaoPaymentSession> getPaymentInfo(String paymentId) {
		return kakaoPaySessionService.getSession(paymentId);
	}

	public void deletePaymentInfo(String paymentId) {
		kakaoPaySessionService.deleteSession(paymentId);
	}

}
