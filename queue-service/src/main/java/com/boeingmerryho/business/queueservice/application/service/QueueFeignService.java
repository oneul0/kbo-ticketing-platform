package com.boeingmerryho.business.queueservice.application.service;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.queueservice.application.dto.request.feign.IssuedTicketDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QueueFeignService {

	private static final String TICKET_INFO_PREFIX = "queue:ticket:";

	private final RedisTemplate<String, Object> redisTemplate;

	public QueueFeignService(
		@Qualifier("redisTemplateForStoreQueueRedis") RedisTemplate<String, Object> redisTemplateForStoreQueueRedis
	) {
		this.redisTemplate = redisTemplateForStoreQueueRedis;
	}

	public void cacheIssuedTicket(IssuedTicketDto dto) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = sdf.format(dto.matchDate());
		String ticketKey = TICKET_INFO_PREFIX + formattedDate + ":" + dto.ticketId();
		redisTemplate.opsForValue().set(ticketKey, dto.userId());
	}
}
