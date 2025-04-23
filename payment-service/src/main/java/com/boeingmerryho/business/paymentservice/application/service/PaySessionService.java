package com.boeingmerryho.business.paymentservice.application.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;

public interface PaySessionService {
	void saveSession(String paymentId, PaymentSession session);

	Optional<PaymentSession> getSession(String paymentId);

	void deleteSession(String paymentId);

	void savePaymentExpiredTime(String paymentId, LocalDateTime expiredTime);

	Optional<LocalDateTime> getPaymentExpiredTime(String paymentId);

	void deletePaymentExpiredTime(String paymentId);

	void savePaymentPrice(String paymentId, Integer price);

	Optional<Integer> getPaymentPrice(String paymentId);

	void deletePaymentPrice(String paymentId);
}
