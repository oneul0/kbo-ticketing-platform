package com.boeingmerryho.business.queueservice.application;

import java.util.Date;

import com.boeingmerryho.business.queueservice.domain.entity.Queue;

public interface QueueHelper {
	public Boolean validateStoreIsActive(Long storeId);

	public Long validateTicket(Date matchDate, Long ticketId);

	public void joinUserInQueue(Long storeId, Long userId, Long ticketId);

	public Integer getUserQueuePosition(Long storeId, Long userId);

	public Boolean removeUserFromQueue(Long storeId, Long userId);

	public Queue saveQueueInfo(Queue queue);
}

