package com.boeingmerryho.business.paymentservice.infrastructure.service;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.paymentservice.application.KakaoPaySessionService;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.KakaoPaymentSession;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoPaySessionServiceImpl implements KakaoPaySessionService {

	private final RedisTemplate<String, KakaoPaymentSession> redisTemplateForKakaoPaymentSession;

	private static final String PREFIX = "kakao:payment:session:";
	private static final Duration TTL = Duration.ofMinutes(10);

	private String buildKey(String paymentId) {
		return PREFIX + paymentId;
	}

	@Override
	public void saveSession(String paymentId, KakaoPaymentSession session) {
		redisTemplateForKakaoPaymentSession.opsForValue().set(buildKey(paymentId), session, TTL);
	}

	@Override
	public Optional<KakaoPaymentSession> getSession(String paymentId) {
		KakaoPaymentSession session = redisTemplateForKakaoPaymentSession.opsForValue().get(buildKey(paymentId));
		return Optional.ofNullable(session);
	}

	@Override
	public void deleteSession(String paymentId) {
		redisTemplateForKakaoPaymentSession.delete(buildKey(paymentId));
	}
}
