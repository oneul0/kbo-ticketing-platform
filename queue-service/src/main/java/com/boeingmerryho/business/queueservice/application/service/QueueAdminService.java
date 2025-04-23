package com.boeingmerryho.business.queueservice.application.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.queueservice.application.QueuePersistenceHelper;
import com.boeingmerryho.business.queueservice.application.QueueRedisHelper;
import com.boeingmerryho.business.queueservice.application.dto.mapper.QueueApplicationMapper;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminCallUserServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminDeleteHistoryServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminDeleteUserServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminSearchHistoryServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminUpdateHistoryServiceDto;
import com.boeingmerryho.business.queueservice.config.aop.DistributedLock;
import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.domain.entity.QueueSearchCriteria;
import com.boeingmerryho.business.queueservice.domain.model.CancelReason;
import com.boeingmerryho.business.queueservice.domain.model.QueueUserInfo;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;
import com.boeingmerryho.business.queueservice.presentation.dto.request.admin.QueueAdminQueueListRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminCallUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminDeleteUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminHistoryListResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminSearchHistoryResponseDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueAdminService {

	private final QueueApplicationMapper queueApplicationMapper;
	private final QueueRedisHelper redisHelper;
	private final QueuePersistenceHelper persistenceHelper;

	@Description("대기열에서 사용자 강제 삭제 메서드")
	@DistributedLock(key = "#dto.storeId")
	public QueueAdminDeleteUserResponseDto deleteUserFromQueue(QueueAdminDeleteUserServiceDto dto) {
		Long storeId = dto.storeId();
		Long userId = dto.userId();

		Integer sequence = redisHelper.getUserSequencePosition(storeId, userId);

		boolean removed = redisHelper.removeUserFromQueue(storeId, userId);

		if (!removed) {
			throw new GlobalException(ErrorCode.CAN_NOT_REMOVE_QUEUE);
		}

		Queue canceledQueue = Queue.cancelQueue(storeId, userId, sequence, CancelReason.ILLEGAL_USED); //todo: 삭제 이유 세분화

		Queue cancelledUser = persistenceHelper.saveQueueInfo(canceledQueue);

		return queueApplicationMapper.toQueueAdminDeleteUserResponseDto(cancelledUser.getStoreId(),
			cancelledUser.getUserId());
	}

	@Description("대기열의 다음 사용자 호출 메서드")
	@DistributedLock(key = "#dto.storeId")
	public QueueAdminCallUserResponseDto callNextUserFromQueue(QueueAdminCallUserServiceDto dto) {
		Long storeId = dto.storeId();
		QueueUserInfo userInfo = redisHelper.getNextUserInQueue(storeId);

		if (userInfo == null) {
			throw new GlobalException(ErrorCode.WAITLIST_EMPTY);
		}

		Integer sequence = redisHelper.getUserSequencePosition(storeId, userInfo.userId());

		redisHelper.removeUserFromQueue(storeId, userInfo.userId());

		Queue canceledQueue = Queue.confirmQueue(storeId, userInfo.userId(), sequence);

		Queue cancelledUser = persistenceHelper.saveQueueInfo(canceledQueue);

		return queueApplicationMapper.toQueueAdminCallUserResponseDto(
			storeId,
			cancelledUser.getUserId(),
			userInfo.rank()
		);
	}

	@Description("가게의 대기열 정보를 가져오는 메서드")
	public Page<QueueAdminHistoryListResponseDto> getQueueList(QueueAdminQueueListRequestDto dto) {
		Long storeId = dto.storeId();
		int page = dto.pageable().getPageNumber();
		int size = dto.pageable().getPageSize();

		String redisKey = redisHelper.getWaitlistInfoPrefix(storeId);

		Set<ZSetOperations.TypedTuple<String>> queueEntries = redisHelper.getUserQueueRange(storeId, page, size);

		if (queueEntries == null || queueEntries.isEmpty()) {
			return Page.empty();
		}

		List<QueueAdminHistoryListResponseDto> result = queueEntries.stream()
			.map(entry -> {
				Long userId = Long.valueOf(Objects.requireNonNull(entry.getValue())); // String → Long
				Integer sequence = Objects.requireNonNull(entry.getScore()).intValue();
				return queueApplicationMapper.toQueueAdminItemListResponseDto(userId, sequence);
			})
			.collect(Collectors.toList());

		Long totalSize = redisHelper.getTotalQueueSize(redisKey);

		return new PageImpl<>(result, PageRequest.of(page, size), totalSize);
	}

	@Description("가게의 대기열 정보 기록을 가져오는 메서드")
	public Page<QueueAdminSearchHistoryResponseDto> getQueueHistory(QueueAdminSearchHistoryServiceDto requestDto) {
		QueueSearchCriteria criteria = persistenceHelper.getQueueSearchCriteria(requestDto);

		Page<Queue> queuePage = persistenceHelper.searchHistoryByDynamicQuery(criteria, requestDto.pageable());

		List<QueueAdminSearchHistoryResponseDto> content = queuePage.getContent().stream()
			.map(queueApplicationMapper::toQueueAdminSearchHistoryResponseDto)
			.toList();

		return new PageImpl<>(content, requestDto.pageable(), queuePage.getTotalElements());

	}

	@Description("가게의 대기열 정보 기록을 삭제하는 메서드")
	public Long deleteQueueHistory(QueueAdminDeleteHistoryServiceDto serviceDto) {
		persistenceHelper.deleteQueueHistoryById(serviceDto.id(), serviceDto.userId());
		return serviceDto.id();
	}

	@Description("가게의 대기열 정보 기록을 수정하는 메서드")
	public Long updateQueueHistory(QueueAdminUpdateHistoryServiceDto serviceDto) {
		Long queueId = serviceDto.id();

		Queue queue = persistenceHelper.findQueueHistoryById(queueId);

		if (serviceDto.status() != null) {
			queue.updateStatus(serviceDto.status());
		}
		if (serviceDto.cancelReason() != null) {
			queue.updateCancelReason(serviceDto.cancelReason());
		}

		persistenceHelper.saveQueueInfo(queue);
		return queue.getId();
	}
}
