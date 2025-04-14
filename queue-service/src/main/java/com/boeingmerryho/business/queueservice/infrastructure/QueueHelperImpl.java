package com.boeingmerryho.business.queueservice.infrastructure;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.queueservice.application.QueueHelper;
import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.domain.repository.QueueRepository;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueHelperImpl implements QueueHelper {

	//todo: value로 주입받기
	private static final String MEMBERSHIP_INFO_PREFIX = "user:membership:info:";
	private static final String TICKET_INFO_PREFIX = "queue:ticket:";
	private static final String STORE_AVAILABILITY_CHECK_PREFIX = "queue:availability:";
	private static final String WAITLIST_INFO_PREFIX = "queue:waitlist:store:";
	private static final Long WAITLIST_INFO_EXPIRE_DAY = 1L;

	private final RedisTemplate<String, Object> redisTemplate;

	private final QueueRepository queueRepository;

	@Override
	public Boolean validateStoreIsActive(Long storeId) {
		return redisTemplate.hasKey(STORE_AVAILABILITY_CHECK_PREFIX + storeId);
	}

	@Override
	public Long validateTicket(Date matchDate, Long ticketId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDate = sdf.format(matchDate);
		String ticketKey = TICKET_INFO_PREFIX + formattedDate + ":" + ticketId;
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
		String today = LocalDate.now().toString();

		String redisKey = String.format(WAITLIST_INFO_PREFIX + storeId + ":" + today);

		Long rank = redisTemplate.opsForZSet().rank(redisKey, userId.toString());
		log.debug("rank : {}", rank);
		if (rank == null) {
			throw new GlobalException(ErrorCode.WAITLIST_NOT_EXIST);
		}

		return rank.intValue() + 1;
	}

	@Override
	public Integer getUserSequencePosition(Long storeId, Long userId) {
		String today = LocalDate.now().toString();

		String redisKey = String.format(WAITLIST_INFO_PREFIX + storeId + ":" + today);

		Double score = redisTemplate.opsForZSet().score(redisKey, userId.toString());
		log.debug("score : {}", score);
		if (score == null) {
			throw new GlobalException(ErrorCode.WAITLIST_NOT_EXIST);
		}

		return score.intValue();
	}

	@Override
	public Boolean removeUserFromQueue(Long storeId, Long userId) {
		String today = LocalDate.now().toString();

		String redisKey = String.format(WAITLIST_INFO_PREFIX + storeId + ":" + today);
		Long removedCount = redisTemplate.opsForZSet().remove(redisKey, userId.toString());

		return removedCount != null && removedCount > 0;
	}

	@Override
	public Queue saveQueueInfo(Queue queue) {
		return queueRepository.save(queue);
	}

	private void setExpireIfAbsent(String key, Duration ttl) {
		Long expire = redisTemplate.getExpire(key);
		if (expire == null || expire == -1) {
			redisTemplate.expire(key, ttl);
		}
	}

}