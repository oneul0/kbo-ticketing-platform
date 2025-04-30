package com.boeingmerryho.business.membershipservice.infrastructure.kafka.consumer;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MembershipRollbackConsumer {

	private final RedisTemplate<String, String> redisTemplate;

	private static final String STOCK_PREFIX = "membership:stock:";
	private static final String USERS_PREFIX = "membership:users:";

	@KafkaListener(
		topics = "membership.reserve.rollback",
		groupId = "membership-rollback-group",
		containerFactory = "rollbackKafkaListenerContainerFactory"
	)
	public void consume(String message) {
		try {
			String[] parts = message.split(":");
			if (parts.length != 2) {
				log.warn("Invalid rollback message format: {}", message);
				return;
			}

			String membershipId = parts[0];
			String userId = parts[1];

			String stockKey = STOCK_PREFIX + membershipId;
			String userSetKey = USERS_PREFIX + membershipId;

			redisTemplate.opsForValue().increment(stockKey);
			redisTemplate.opsForSet().remove(userSetKey, userId);

			log.info("Rollback success: membershipId={}, userId={}", membershipId, userId);
		} catch (Exception e) {
			log.error("Failed to process rollback message: {}", message, e);
		}
	}
}
