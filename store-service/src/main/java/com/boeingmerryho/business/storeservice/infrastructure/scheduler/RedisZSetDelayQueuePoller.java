package com.boeingmerryho.business.storeservice.infrastructure.scheduler;

import java.time.Instant;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisZSetDelayQueuePoller {

	private final MeterRegistry meterRegistry;
	private final StringRedisTemplate redisTemplate;
	private final KafkaTemplate<String, String> kafkaTemplate;

	private static final String DELAY_ZSET_KEY = "zset:queue:schedule";
	private static final String SCHEDULE_TOPIC = "store.queue.schedule";

	private volatile double averagePollingDelayMillis = 0.0;

	@PostConstruct
	public void initMetrics() {
		Gauge.builder("redis_zset_polling_average_delay_millis", () -> averagePollingDelayMillis)
			.description("Redis ZSet 폴링 평균 지연 시간 (ms)")
			.register(meterRegistry);
	}

	@Scheduled(fixedDelay = 5000)
	public void poll() {
		long now = Instant.now().toEpochMilli();

		Set<String> readyMessages = redisTemplate.opsForZSet().rangeByScore(DELAY_ZSET_KEY, 0, now);
		if (readyMessages == null || readyMessages.isEmpty())
			return;

		double totalDelay = 0;
		int messageCount = readyMessages.size();

		for (String message : readyMessages) {
			try {
				Double score = redisTemplate.opsForZSet().score(DELAY_ZSET_KEY, message);
				if (score == null) {
					log.warn("Score not found for message: {}", message);
					continue;
				}
				
				long scheduledTime = score.longValue();
				long delayMillis = now - scheduledTime;
				totalDelay += delayMillis;

				kafkaTemplate.send(SCHEDULE_TOPIC, message);
				redisTemplate.opsForZSet().remove(DELAY_ZSET_KEY, message);

				log.info("Sent message: {}", message);
			} catch (Exception e) {
				log.error("Failed to send message: {}", message, e);
			}
		}
		averagePollingDelayMillis = totalDelay / messageCount;
	}
}
// TODO 특정 날짜의 특정 가게 정보 prefix 를 키로 둔 time 값을 저장