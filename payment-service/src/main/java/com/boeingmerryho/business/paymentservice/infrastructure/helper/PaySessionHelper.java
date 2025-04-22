package com.boeingmerryho.business.paymentservice.infrastructure.helper;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;
import com.boeingmerryho.business.paymentservice.application.service.PaySessionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaySessionHelper {

	private final PaySessionService paySessionService;

	public void savePaymentInfo(String paymentId, PaymentSession session) {
		paySessionService.saveSession(paymentId, session);
	}

	public Optional<PaymentSession> getPaymentInfo(String paymentId) {
		return paySessionService.getSession(paymentId);
	}

	public void deletePaymentInfo(String paymentId) {
		paySessionService.deleteSession(paymentId);
	}

	public void savePaymentExpiredTime(String paymentId, LocalDateTime expiredTime) {
		paySessionService.savePaymentExpiredTime(paymentId, expiredTime);
	}

	public Optional<LocalDateTime> getPaymentExpiredTime(String paymentId) {
		return paySessionService.getPaymentExpiredTime(paymentId);
	}

	public void deletePaymentExpiredTime(String paymentId) {
		paySessionService.deletePaymentExpiredTime(paymentId);
	}

	public void savePaymentPrice(String paymentId, Integer price) {
		paySessionService.savePaymentPrice(paymentId, price);
	}

	public Optional<Integer> getPaymentPrice(String paymentId) {
		return paySessionService.getPaymentPrice(paymentId);
	}

	public void deletePaymentPrice(String paymentId) {
		paySessionService.deletePaymentPrice(paymentId);
	}

}
