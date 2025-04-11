package com.boeingmerryho.business.storeservice.infrastructure.helper;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreQueueAdminHelper {

	private final StringRedisTemplate redisTemplate;

	private static final String QUEUE_KEY_PREFIX = "queue:availability:";

	public void enableQueue(Long storeId) {
		redisTemplate.opsForValue().set(QUEUE_KEY_PREFIX + storeId, "true");
	}

	public void disableQueue(Long storeId) {
		redisTemplate.delete(QUEUE_KEY_PREFIX + storeId);
	}

	public boolean isQueueAvailable(Long storeId) {
		String key = "queue:availability:" + storeId;
		return redisTemplate.hasKey(key) && "true".equals(redisTemplate.opsForValue().get(key));
	}
}
