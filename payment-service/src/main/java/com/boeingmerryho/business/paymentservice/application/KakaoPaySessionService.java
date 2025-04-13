package com.boeingmerryho.business.paymentservice.application;

import java.util.Optional;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPaymentSession;

public interface KakaoPaySessionService {
	void saveSession(String orderId, KakaoPaymentSession session);

	Optional<KakaoPaymentSession> getSession(String orderId);

	void deleteSession(String orderId);
}
