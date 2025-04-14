package com.boeingmerryho.business.queueservice.application;

import java.util.Date;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ZSetOperations;

import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminSearchHistoryServiceDto;
import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.domain.entity.QueueSearchCriteria;
import com.boeingmerryho.business.queueservice.domain.model.QueueUserInfo;

public interface QueueHelper {
	public Boolean validateStoreIsActive(Long storeId);

	public Long validateTicket(Date matchDate, Long ticketId);

	public void joinUserInQueue(Long storeId, Long userId, Long ticketId);

	public Integer getUserQueuePosition(Long storeId, Long userId);

	public Integer getUserSequencePosition(Long storeId, Long userId);

	public Boolean removeUserFromQueue(Long storeId, Long userId);

	public Queue saveQueueInfo(Queue queue);

	QueueUserInfo getNextUserInQueue(Long storeId);

	String getWaitlistInfoPrefix(Long storeId);

	public Set<ZSetOperations.TypedTuple<String>> getUserQueueRange(Long storeId, int page, int size);

	Long getTotalQueueSize(String redisKey);

	QueueSearchCriteria getQueueSearchCriteria(QueueAdminSearchHistoryServiceDto requestDto);

	Page<Queue> searchHistoryByDynamicQuery(QueueSearchCriteria criteria, Pageable pageable);
}

