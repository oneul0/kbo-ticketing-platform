package com.boeingmerryho.business.queueservice.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.domain.entity.QueueSearchCriteria;
import com.boeingmerryho.business.queueservice.domain.model.QueueStatus;

public interface CustomQueueRepository {

	Page<Queue> findDynamicQuery(QueueSearchCriteria criteria, Pageable pageable);

	Optional<List<Queue>> findQueueHistoryByStatus(QueueStatus status, Boolean isDeleted);

	Optional<Queue> findActiveHistoryById(Long id);

}
