package com.boeingmerryho.business.paymentservice.infrastructure.config.auth;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import io.github.boeingmerryho.commonlibrary.interceptor.UserInfoProvider;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUserInfoProvider implements UserInfoProvider {
	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public Map<String, Object> getUserInfo(long userId) {
		String key = "user:info:" + userId;
		Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

		return entries.entrySet().stream()
			.collect(Collectors.toMap(
				e -> e.getKey().toString(),
				Map.Entry::getValue
			));
	}
}