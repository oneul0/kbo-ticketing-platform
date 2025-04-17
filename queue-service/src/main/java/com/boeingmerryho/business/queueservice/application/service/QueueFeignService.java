package com.boeingmerryho.business.queueservice.application.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.queueservice.application.dto.request.feign.IssuedTicketDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QueueFeignService {

	private static final String TICKET_INFO_PREFIX = "queue:ticket:";
	private static final String TICKET_USER_INFO_PREFIX = "ticket:user:";

	private final RedisTemplate<String, Object> redisTemplateForStoreQueueRedis;

	public QueueFeignService(
		@Qualifier("redisTemplateForStoreQueueRedis") RedisTemplate<String, Object> redisTemplateForStoreQueueRedis
	) {
		this.redisTemplateForStoreQueueRedis = redisTemplateForStoreQueueRedis;
	}

	public void cacheIssuedTicket(IssuedTicketDto dto) {
		LocalDate today = LocalDate.now();
		LocalDate matchDate = dto.matchDate().toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate();

		long ttlDays = Math.max(ChronoUnit.DAYS.between(today, matchDate), 1);
		String dateKey = TICKET_INFO_PREFIX + matchDate;
		String userKey = TICKET_USER_INFO_PREFIX + dto.ticketId();

		redisTemplateForStoreQueueRedis.opsForSet().add(dateKey, dto.ticketId().toString());

		redisTemplateForStoreQueueRedis.opsForValue().set(userKey, dto.userId().toString(), ttlDays, TimeUnit.DAYS);

		if (!Boolean.TRUE.equals(redisTemplateForStoreQueueRedis.hasKey(dateKey)) ||
			redisTemplateForStoreQueueRedis.getExpire(dateKey, TimeUnit.SECONDS) == -1) {
			redisTemplateForStoreQueueRedis.expire(dateKey, ttlDays, TimeUnit.DAYS);
		}
	}

}
