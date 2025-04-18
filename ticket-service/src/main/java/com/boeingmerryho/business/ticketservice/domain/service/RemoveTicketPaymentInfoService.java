package com.boeingmerryho.business.ticketservice.domain.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RemoveTicketPaymentInfoService {

	private final RedisTemplate<String, Object> redisTemplate;

	public RemoveTicketPaymentInfoService(
		@Qualifier("ticketRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void removeTicketPaymentInfo(Long userId) {
		String redisKey = "ticket:payment:" + userId;
		redisTemplate.delete(redisKey);
	}
}
