package com.boeingmerryho.business.queueservice.infrastructure;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.queueservice.application.QueueHelper;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminSearchHistoryServiceDto;
import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.domain.entity.QueueSearchCriteria;
import com.boeingmerryho.business.queueservice.domain.model.QueueUserInfo;
import com.boeingmerryho.business.queueservice.domain.repository.CustomQueueRepository;
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
	private final CustomQueueRepository customQueueRepository;

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
		String redisKey = getWaitlistInfoPrefix(storeId);
		String seqKey = redisKey + ":seq";

		try {
			Long order = redisTemplate.opsForValue().increment(seqKey);

			redisTemplate.opsForZSet().add(redisKey, userId.toString(), Objects.requireNonNull(order).doubleValue());
		} catch (RedisConnectionFailureException e) {
			throw new GlobalException(ErrorCode.QUEUE_JOIN_FAIL);
		}

		setExpireIfAbsent(redisKey, Duration.ofDays(WAITLIST_INFO_EXPIRE_DAY));
		setExpireIfAbsent(seqKey, Duration.ofDays(WAITLIST_INFO_EXPIRE_DAY));
	}

	@Override
	public Integer getUserQueuePosition(Long storeId, Long userId) {
		String redisKey = getWaitlistInfoPrefix(storeId);

		Long rank = redisTemplate.opsForZSet().rank(redisKey, userId.toString());
		log.debug("rank : {}", rank);
		if (rank == null) {
			throw new GlobalException(ErrorCode.WAITLIST_NOT_EXIST);
		}

		return rank.intValue() + 1;
	}

	@Override
	public Integer getUserSequencePosition(Long storeId, Long userId) {
		String redisKey = getWaitlistInfoPrefix(storeId);

		Double score = redisTemplate.opsForZSet().score(redisKey, userId.toString());
		log.debug("score : {}", score);
		if (score == null) {
			throw new GlobalException(ErrorCode.WAITLIST_NOT_EXIST);
		}

		return score.intValue();
	}

	@Override
	public Boolean removeUserFromQueue(Long storeId, Long userId) {
		String redisKey = getWaitlistInfoPrefix(storeId);
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

	@Override
	public QueueUserInfo getNextUserInQueue(Long storeId) {

		String redisKey = getWaitlistInfoPrefix(storeId);

		Set<Object> userIds = redisTemplate.opsForZSet().range(redisKey, 0, 0);
		if (userIds == null || userIds.isEmpty()) {
			return null;
		}

		String userIdStr = (String)userIds.iterator().next();
		Long userId = Long.parseLong(userIdStr);

		Long rank = redisTemplate.opsForZSet().rank(redisKey, userIdStr);
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

		Set<ZSetOperations.TypedTuple<String>> result = (Set<ZSetOperations.TypedTuple<String>>)(Set<?>)redisTemplate.opsForZSet()
			.rangeWithScores(redisKey, start, end);

		return result;
	}

	@Override
	public Long getTotalQueueSize(String redisKey) {
		return redisTemplate.opsForZSet().size(redisKey);
	}

	@Override
	public QueueSearchCriteria getQueueSearchCriteria(QueueAdminSearchHistoryServiceDto requestDto) {
		QueueSearchCriteria criteria = QueueSearchCriteria.builder()
			.storeId(requestDto.storeId())
			.userId(requestDto.userId())
			.status(requestDto.status())
			.cancelReason(requestDto.cancelReason())
			.startDate(requestDto.startDate())
			.endDate(requestDto.endDate())
			.build();

		return criteria;
	}

	@Override
	public Page<Queue> searchHistoryByDynamicQuery(QueueSearchCriteria criteria, Pageable pageable) {
		return customQueueRepository.findDynamicQuery(criteria, pageable);
	}
}