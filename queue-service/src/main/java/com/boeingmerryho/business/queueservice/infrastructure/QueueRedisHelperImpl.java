package com.boeingmerryho.business.queueservice.infrastructure;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.queueservice.application.QueueRedisHelper;
import com.boeingmerryho.business.queueservice.domain.model.QueueUserInfo;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QueueRedisHelperImpl implements QueueRedisHelper {

	private static final String TICKET_INFO_PREFIX = "queue:ticket:";
	private static final String STORE_AVAILABILITY_CHECK_PREFIX = "queue:availability:";
	private static final String WAITLIST_INFO_PREFIX = "queue:waitlist:store:";
	private static final String TICKET_USER_INFO_PREFIX = "ticket:user:";

	private static final Long WAITLIST_INFO_EXPIRE_DAY = 1L;

	private final RedisTemplate<String, Object> redisTemplateForStoreQueueRedis;

	public QueueRedisHelperImpl(
		@Qualifier("redisTemplateForStoreQueueRedis") RedisTemplate<String, Object> redisTemplateForStoreQueueRedis
	) {
		this.redisTemplateForStoreQueueRedis = redisTemplateForStoreQueueRedis;
	}

	@Override
	public Boolean validateStoreIsActive(Long storeId) {
		String storeAvailabilityKey = STORE_AVAILABILITY_CHECK_PREFIX + storeId;

		Boolean isActive = (Boolean)redisTemplateForStoreQueueRedis.opsForValue().get(storeAvailabilityKey);

		return Boolean.TRUE.equals(isActive);
	}

	@Override
	public Long validateTicket(Date matchDate, Long ticketId) {
		LocalDate date = parseDateToLocalDate(matchDate);

		String dateKey = TICKET_INFO_PREFIX + date;
		String userKey = TICKET_USER_INFO_PREFIX + ticketId;

		Boolean exists = isExistsInRedisSet(dateKey, ticketId.toString());

		if (!Boolean.TRUE.equals(exists)) {
			throw new GlobalException(ErrorCode.TICKET_IS_NOT_ACTIVATED);
		}

		String userIdValue = getOpsForValueInRedisWithErrorCode(userKey, ErrorCode.TICKET_NOT_FOUND);
		log.info("userIdValue : {}", userIdValue);
		return Long.parseLong(userIdValue);
	}

	@Override
	public String getOpsForValueInRedisWithErrorCode(String key, ErrorCode errorCode) {
		return Optional.ofNullable(redisTemplateForStoreQueueRedis.opsForValue().get(key))
			.map(Object::toString)
			.orElseThrow(() -> new GlobalException(errorCode));
	}

	@Override
	public Boolean isExistsInRedisSet(String setKey, String elementsKey) {
		return redisTemplateForStoreQueueRedis.opsForSet()
			.isMember(setKey, elementsKey);
	}

	@Override
	public LocalDate parseDateToLocalDate(Date date) {
		return date.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate();
	}

	@Override
	public void joinUserInQueue(Long storeId, Long userId, Long ticketId) {
		String redisKey = getWaitlistInfoPrefix(storeId);
		String seqKey = redisKey + ":seq";

		try {
			Long order = redisTemplateForStoreQueueRedis.opsForValue().increment(seqKey);

			redisTemplateForStoreQueueRedis.opsForZSet()
				.add(redisKey, userId.toString(), Objects.requireNonNull(order).doubleValue());
		} catch (RedisConnectionFailureException e) {
			throw new GlobalException(ErrorCode.QUEUE_JOIN_FAIL);
		}

		setExpireIfAbsent(redisKey, Duration.ofDays(WAITLIST_INFO_EXPIRE_DAY));
		setExpireIfAbsent(seqKey, Duration.ofDays(WAITLIST_INFO_EXPIRE_DAY));
	}

	@Override
	public Integer getUserQueuePosition(Long storeId, Long userId) {
		String redisKey = getWaitlistInfoPrefix(storeId);

		Long rank = redisTemplateForStoreQueueRedis.opsForZSet().rank(redisKey, userId.toString());
		log.debug("rank : {}", rank);
		if (rank == null) {
			throw new GlobalException(ErrorCode.WAITLIST_NOT_EXIST);
		}

		return rank.intValue() + 1;
	}

	@Override
	public Integer getUserSequencePosition(Long storeId, Long userId) {
		String redisKey = getWaitlistInfoPrefix(storeId);

		Double score = redisTemplateForStoreQueueRedis.opsForZSet().score(redisKey, userId.toString());
		log.debug("score : {}", score);
		if (score == null) {
			throw new GlobalException(ErrorCode.WAITLIST_NOT_EXIST);
		}

		return score.intValue();
	}

	@Override
	public Boolean removeUserFromQueue(Long storeId, Long userId) {
		String redisKey = getWaitlistInfoPrefix(storeId);
		Long removedCount = redisTemplateForStoreQueueRedis.opsForZSet().remove(redisKey, userId.toString());

		return removedCount != null && removedCount > 0;
	}

	private void setExpireIfAbsent(String key, Duration ttl) {
		Long expire = redisTemplateForStoreQueueRedis.getExpire(key);
		if (expire == null || expire == -1) {
			redisTemplateForStoreQueueRedis.expire(key, ttl);
		}
	}

	@Override
	public QueueUserInfo getNextUserInQueue(Long storeId) {

		String redisKey = getWaitlistInfoPrefix(storeId);

		Set<Object> userIds = redisTemplateForStoreQueueRedis.opsForZSet().range(redisKey, 0, 0);
		if (userIds == null || userIds.isEmpty()) {
			return null;
		}

		String userIdStr = (String)userIds.iterator().next();
		Long userId = Long.parseLong(userIdStr);

		Long rank = redisTemplateForStoreQueueRedis.opsForZSet().rank(redisKey, userIdStr);
		if (rank == null) {
			throw new GlobalException(ErrorCode.WAITLIST_NOT_EXIST);
		}

		return new QueueUserInfo(userId, rank.intValue() + 1);
	}

	@Override
	public String getWaitlistInfoPrefix(Long storeId) {
		String today = LocalDate.now().toString();
		String redisKey = String.format(WAITLIST_INFO_PREFIX + storeId + ":" + today);
		return redisKey;
	}

	@Override
	public Set<ZSetOperations.TypedTuple<String>> getUserQueueRange(Long storeId, int page, int size) {
		String today = LocalDate.now().toString();
		String redisKey = String.format(WAITLIST_INFO_PREFIX + storeId + ":" + today);

		long start = (long)page * size;
		long end = start + size - 1;

		Set<ZSetOperations.TypedTuple<String>> result = (Set<ZSetOperations.TypedTuple<String>>)(Set<?>)redisTemplateForStoreQueueRedis.opsForZSet()
			.rangeWithScores(redisKey, start, end);

		return result;
	}

	@Override
	public Long getTotalQueueSize(String redisKey) {
		return redisTemplateForStoreQueueRedis.opsForZSet().size(redisKey);
	}

}