package com.boeingmerryho.business.queueservice.infrastructure;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.queueservice.application.QueueJoinHelper;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueJoinHelperImpl implements QueueJoinHelper {

	//todo: value로 주입받기
	private static final String MEMBERSHIP_INFO_PREFIX = "user:membership:info:";
	private static final String TICKET_INFO_PREFIX = "queue:ticket:";
	private static final String STORE_AVAILABILITY_CHECK_PREFIX = "queue:availability:";
	private static final String WAITLIST_INFO_PREFIX = "queue:waitlist:store:";
	private static final Long WAITLIST_INFO_EXPIRE_DAY = 1L;


	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public Boolean validateStoreIsActive(Long storeId) {
		return redisTemplate.hasKey(STORE_AVAILABILITY_CHECK_PREFIX + storeId);
	}

	@Override
	public Long validateTicket(Date matchDate, Long ticketId) {
		String ticketKey = TICKET_INFO_PREFIX + matchDate.toString() + ":" + ticketId;
		Optional<String> userIdValue = Optional.ofNullable(redisTemplate.opsForValue().get(ticketKey))
			.map(Object::toString);
		if (userIdValue.isEmpty()) {
			throw new GlobalException(ErrorCode.TICKET_IS_NOT_ACTIVATED);
		}
		return Long.parseLong(userIdValue.get());
	}

	@Override
	public void joinUserInQueue(Long storeId, Long userId, Long ticketId) {
		String today = LocalDate.now().toString();

		String queueKey = WAITLIST_INFO_PREFIX + storeId + ":" + today;
		String seqKey = queueKey + ":seq";

		try {
			Long order = redisTemplate.opsForValue().increment(seqKey);

			redisTemplate.opsForZSet().add(queueKey, userId.toString(), Objects.requireNonNull(order).doubleValue());
		} catch (RedisConnectionFailureException e) {
			throw new GlobalException(ErrorCode.QUEUE_JOIN_FAIL);
		}

		setExpireIfAbsent(queueKey, Duration.ofDays(WAITLIST_INFO_EXPIRE_DAY));
		setExpireIfAbsent(seqKey, Duration.ofDays(WAITLIST_INFO_EXPIRE_DAY));
	}

	@Override
	public Integer getUserQueuePosition(Long storeId, Long userId) {
		return 0;
	}

	private void setExpireIfAbsent(String key, Duration ttl) {
		Long expire = redisTemplate.getExpire(key);
		if (expire == null || expire == -1) {
			redisTemplate.expire(key, ttl);
		}
	}
}