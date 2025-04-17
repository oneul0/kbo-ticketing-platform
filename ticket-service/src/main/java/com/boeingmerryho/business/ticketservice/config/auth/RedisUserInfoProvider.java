package com.boeingmerryho.business.ticketservice.config.auth;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import io.github.boeingmerryho.commonlibrary.interceptor.UserInfoProvider;

@Component
public class RedisUserInfoProvider implements UserInfoProvider {

	private final RedisTemplate<String, Object> redisTemplate;

	public RedisUserInfoProvider(
		@Qualifier("commonRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

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
