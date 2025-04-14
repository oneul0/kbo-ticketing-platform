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

	private static final String PAYMENT_SESSION_PREFIX = "payment:session:";
	private static final String PAYMENT_EXPIRED_TIME_PREFIX = "payment:expired:";
	private static final Duration TTL = Duration.ofMinutes(10);

	private String buildKey(String prefix, String paymentId) {
		return prefix + paymentId;
	}

	@Override
	public void saveSession(String paymentId, PaymentSession session) {
		redisTemplateForPaymentSession.opsForValue().set(buildKey(PAYMENT_SESSION_PREFIX, paymentId), session, TTL);
	}

	@Override
	public Optional<PaymentSession> getSession(String paymentId) {
		PaymentSession session = redisTemplateForPaymentSession.opsForValue()
			.get(buildKey(PAYMENT_SESSION_PREFIX, paymentId));
		return Optional.ofNullable(session);
	}

	@Override
	public void deleteSession(String paymentId) {
		redisTemplateForPaymentSession.delete(buildKey(PAYMENT_SESSION_PREFIX, paymentId));
	}

	@Override
	public void savePaymentExpiredTime(String paymentId, LocalDateTime expiredTime) {
		redisTemplateForPaymentExpiredTime.opsForValue()
			.set(buildKey(PAYMENT_EXPIRED_TIME_PREFIX, paymentId), expiredTime);
	}

	@Override
	public Optional<LocalDateTime> getPaymentExpiredTime(String paymentId) {
		LocalDateTime expiredTime = redisTemplateForPaymentExpiredTime.opsForValue()
			.get(buildKey(PAYMENT_EXPIRED_TIME_PREFIX, paymentId));
		return Optional.ofNullable(expiredTime);
	}

	@Override
	public void deletePaymentExpiredTime(String paymentId) {
		redisTemplateForPaymentExpiredTime.delete(buildKey(PAYMENT_EXPIRED_TIME_PREFIX, paymentId));
	}
}
