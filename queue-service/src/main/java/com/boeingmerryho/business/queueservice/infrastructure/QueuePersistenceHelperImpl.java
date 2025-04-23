package com.boeingmerryho.business.queueservice.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.queueservice.application.QueuePersistenceHelper;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminSearchHistoryServiceDto;
import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.domain.entity.QueueSearchCriteria;
import com.boeingmerryho.business.queueservice.domain.repository.CustomQueueRepository;
import com.boeingmerryho.business.queueservice.domain.repository.QueueRepository;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QueuePersistenceHelperImpl implements QueuePersistenceHelper {

	private final QueueRepository queueRepository;
	private final CustomQueueRepository customQueueRepository;

	public QueuePersistenceHelperImpl(
		QueueRepository queueRepository,
		CustomQueueRepository customQueueRepository
	) {
		this.queueRepository = queueRepository;
		this.customQueueRepository = customQueueRepository;
	}

	@Override
	public Queue saveQueueInfo(Queue queue) {
		return queueRepository.save(queue);
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

	@Override
	public Queue findQueueHistoryById(Long id) {
		return queueRepository.findAllById(id)
			.orElseThrow(() -> new GlobalException(ErrorCode.QUEUE_HISTORY_NOT_FOUND));
	}

	@Override
	public void deleteQueueHistoryById(Long id, Long userId) {
		Queue queue = findQueueHistoryById(id);
		queue.softDelete(userId);
	}
}