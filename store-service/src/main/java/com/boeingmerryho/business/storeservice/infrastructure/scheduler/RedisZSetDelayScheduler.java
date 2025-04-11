package com.boeingmerryho.business.storeservice.infrastructure.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.storeservice.infrastructure.kafka.scheduler.message.StoreQueueScheduleMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisZSetDelayScheduler implements DelayScheduler {

	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	private static final String DELAY_ZSET_KEY = "zset:queue:schedule";

	@Override
	public void schedule(LocalDateTime executeAt, StoreQueueScheduleMessage message) {
		try {
			String serialized = objectMapper.writeValueAsString(message);
			double score = executeAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

			redisTemplate.opsForZSet().add(DELAY_ZSET_KEY, serialized, score);
			log.info("Scheduled message: {} at {}", message, executeAt);
		} catch (Exception e) {
			log.error("Failed to schedule message: {}", message, e);
		}
	}
}
