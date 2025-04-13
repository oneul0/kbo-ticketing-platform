package com.boeingmerryho.business.paymentservice.application;

import java.util.Optional;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;

public interface PaySessionService {
	void saveSession(String orderId, PaymentSession session);

	Optional<PaymentSession> getSession(String orderId);

	void deleteSession(String orderId);
}
