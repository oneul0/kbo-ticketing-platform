package com.boeingmerryho.business.paymentservice.infrastructure;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.PaySessionService;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;

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

}
