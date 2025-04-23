package com.boeingmerryho.business.queueservice.application;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

import org.springframework.data.redis.core.ZSetOperations;

import com.boeingmerryho.business.queueservice.domain.model.QueueUserInfo;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;

public interface QueueRedisHelper {
	public Boolean validateStoreIsActive(Long storeId);

	public Long validateTicket(Date matchDate, Long ticketId);

	String getOpsForValueInRedisWithErrorCode(String key, ErrorCode errorCode);

	Boolean isExistsInRedisSet(String setKey, String elementsKey);

	LocalDate parseDateToLocalDate(Date date);

	public void joinUserInQueue(Long storeId, Long userId, Long ticketId);

	public Integer getUserQueuePosition(Long storeId, Long userId);

	public Integer getUserSequencePosition(Long storeId, Long userId);

	public Boolean removeUserFromQueue(Long storeId, Long userId);

	QueueUserInfo getNextUserInQueue(Long storeId);

	String getWaitlistInfoPrefix(Long storeId);

	public Set<ZSetOperations.TypedTuple<String>> getUserQueueRange(Long storeId, int page, int size);

	Long getTotalQueueSize(String redisKey);

}

