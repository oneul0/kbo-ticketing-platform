package com.boeingmerryho.business.paymentservice.application;

import java.time.LocalDateTime;
import java.util.Optional;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;

public interface PaySessionService {
	void saveSession(String paymentId, PaymentSession session);

	Optional<PaymentSession> getSession(String paymentId);

	void deleteSession(String paymentId);

	void savePaymentExpiredTime(String paymentId, LocalDateTime expiredTime);
}
