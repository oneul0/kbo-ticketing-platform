package com.boeingmerryho.business.membershipservice.infrastructure.kafka.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisExpirationListener extends KeyExpirationEventMessageListener {

	private final RedisTemplate<String, String> redisTemplate;
	private final KafkaTemplate<String, String> kafkaTemplate;

	private static final String TTL_PREFIX = "membership:ttl:";
	private static final String MAP_PREFIX = "membership:user:map:";
	private static final String TOPIC = "membership.reserve.rollback";

	public RedisExpirationListener(RedisMessageListenerContainer container,
		RedisTemplate<String, String> redisTemplate,
		KafkaTemplate<String, String> kafkaTemplate) {
		super(container);
		this.redisTemplate = redisTemplate;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		String expiredKey = message.toString();

		if (!expiredKey.startsWith(TTL_PREFIX))
			return;

		String userId = expiredKey.replace(TTL_PREFIX, "");
		String mapKey = MAP_PREFIX + userId;

		String membershipId = redisTemplate.opsForValue().get(mapKey);

		if (membershipId != null) {
			String payload = membershipId + ":" + userId;
			kafkaTemplate.send(TOPIC, payload);
			log.info("Published rollback event to Kafka: {}", payload);
		} else {
			log.warn("Rollback target not found for expired userId={}", userId);
		}
	}
}

