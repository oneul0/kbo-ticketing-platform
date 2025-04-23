package com.boeingmerryho.business.ticketservice.infrastructure.repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketPaymentRepository;

@Repository
public class TicketPaymentRedisRepository implements TicketPaymentRepository {

	private static final int EXPIRE_MINUTES = 8;
	private static final String TICKET_PAYMENT_KEY_PREFIX = "ticket:payment:";
	private static final String TICKET_PAYMENT_DLQ_KEY = "ticket:dlq:payment";

	private final RedisTemplate<String, Object> redisTemplate;

	public TicketPaymentRedisRepository(@Qualifier("ticketRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void savePaymentInfo(Long userId, Map<String, Object> paymentInfo) {
		String redisKey = TICKET_PAYMENT_KEY_PREFIX + userId;
		redisTemplate.opsForHash().putAll(redisKey, paymentInfo);
		redisTemplate.expire(redisKey, Duration.ofMinutes(EXPIRE_MINUTES));
	}

	@Override
	public Map<Object, Object> getPaymentInfo(Long userId) {
		String redisKey = TICKET_PAYMENT_KEY_PREFIX + userId;
		return redisTemplate.opsForHash().entries(redisKey);
	}

	@Override
	public void deletePaymentInfo(Long userId) {
		String redisKey = TICKET_PAYMENT_KEY_PREFIX + userId;
		redisTemplate.delete(redisKey);
	}

	@Override
	public void saveFailedPayment(List<Ticket> tickets) {
		for (Ticket ticket : tickets) {
			redisTemplate.opsForHash().put(TICKET_PAYMENT_DLQ_KEY, String.valueOf(ticket.getId()), ticket.getTicketNo());
		}
	}
}
