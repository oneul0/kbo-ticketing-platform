package com.boeingmerryho.business.queueservice.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminSearchHistoryServiceDto;
import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.domain.entity.QueueSearchCriteria;

public interface QueuePersistenceHelper {

	public Queue saveQueueInfo(Queue queue);

	QueueSearchCriteria getQueueSearchCriteria(QueueAdminSearchHistoryServiceDto requestDto);

	Page<Queue> searchHistoryByDynamicQuery(QueueSearchCriteria criteria, Pageable pageable);

	Queue findQueueHistoryById(Long id);

	void deleteQueueHistoryById(Long id, Long userId);
}

