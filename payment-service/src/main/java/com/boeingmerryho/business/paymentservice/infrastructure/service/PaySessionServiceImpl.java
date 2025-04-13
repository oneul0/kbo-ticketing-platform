package com.boeingmerryho.business.paymentservice.infrastructure.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.paymentservice.application.PaySessionService;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaySessionServiceImpl implements PaySessionService {

	private final RedisTemplate<String, PaymentSession> redisTemplateForPaymentSession;
	private final RedisTemplate<String, LocalDateTime> redisTemplateForPaymentExpiredTime;

	private static final String PREFIX = "payment:session:";
	private static final Duration TTL = Duration.ofMinutes(10);

	private String buildKey(String paymentId) {
		return PREFIX + paymentId;
	}

	@Override
	public void saveSession(String paymentId, PaymentSession session) {
		redisTemplateForPaymentSession.opsForValue().set(buildKey(paymentId), session, TTL);
	}

	@Override
	public Optional<PaymentSession> getSession(String paymentId) {
		PaymentSession session = redisTemplateForPaymentSession.opsForValue().get(buildKey(paymentId));
		return Optional.ofNullable(session);
	}

	@Override
	public void deleteSession(String paymentId) {
		redisTemplateForPaymentSession.delete(buildKey(paymentId));
	}

	@Override
	public void savePaymentExpiredTime(String paymentId, LocalDateTime expiredTime) {
		redisTemplateForPaymentExpiredTime.opsForValue().set(buildKey(paymentId), expiredTime);
	}
}
