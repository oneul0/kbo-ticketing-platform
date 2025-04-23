package com.boeingmerryho.business.storeservice.infrastructure.scheduler;

import java.time.Instant;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisZSetDelayQueuePoller {

	private final StringRedisTemplate redisTemplate;
	private final KafkaTemplate<String, String> kafkaTemplate;

	private static final String DELAY_ZSET_KEY = "zset:queue:schedule";
	private static final String SCHEDULE_TOPIC = "store.queue.schedule";

	@Scheduled(fixedDelay = 5000)
	public void poll() {
		long now = Instant.now().toEpochMilli();

		Set<String> readyMessages = redisTemplate.opsForZSet().rangeByScore(DELAY_ZSET_KEY, 0, now);
		if (readyMessages == null || readyMessages.isEmpty())
			return;

		for (String message : readyMessages) {
			try {
				kafkaTemplate.send(SCHEDULE_TOPIC, message);
				redisTemplate.opsForZSet().remove(DELAY_ZSET_KEY, message);
				log.info("Sent message: {}", message);
			} catch (Exception e) {
				log.error("Failed to send message: {}", message, e);
			}
		}
	}
}
// TODO 특정 날짜의 특정 가게 정보 prefix 를 키로 둔 time 값을 저장