package com.boeingmerryho.business.queueservice.application.service;

import java.util.Date;
import java.util.Objects;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.queueservice.application.QueuePersistenceHelper;
import com.boeingmerryho.business.queueservice.application.QueueRedisHelper;
import com.boeingmerryho.business.queueservice.application.dto.mapper.QueueApplicationMapper;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueCancelServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueJoinServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueUserSequenceServiceDto;
import com.boeingmerryho.business.queueservice.config.aop.DistributedLock;
import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.domain.model.CancelReason;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;
import com.boeingmerryho.business.queueservice.infrastructure.QueueMetricsHelperImpl;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueCancelResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueJoinResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueUserRankResponseDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final QueueApplicationMapper queueApplicationMapper;
	private final QueueRedisHelper redisHelper;
	private final QueuePersistenceHelper persistenceHelper;
	private final QueueMetricsHelperImpl queueMetricsHelper;

	@Description("대기열에 등록하는 메서드")
	@DistributedLock(key = "#dto.storeId")
	@Counted(value = "queue.request.count", description = "줄서기 요청 수")
	public QueueJoinResponseDto joinQueue(QueueJoinServiceDto dto) throws InterruptedException {

		Timer.Sample joinTimer = Timer.start(Metrics.globalRegistry);

		Long storeId = dto.storeId();
		Long userId = dto.userId();
		Long ticketId = dto.ticketId();
		Date matchDate = new Date();

		if (!redisHelper.validateStoreIsActive(storeId)) {
			throw new GlobalException(ErrorCode.STORE_IS_NOT_ACTIVATED);
		}

		Long ticketUserId = redisHelper.validateTicket(matchDate, ticketId);
		log.debug("ticketUserId : {}", ticketUserId);
		if (!Objects.equals(userId, ticketUserId)) {
			throw new GlobalException(ErrorCode.USER_IS_NOT_MATCHED);
		}

		redisHelper.joinUserInQueue(storeId, userId, ticketId);

		Integer rank = redisHelper.getUserQueuePosition(storeId, userId);
		queueMetricsHelper.incrementWaitingUsers();
		queueMetricsHelper.countQueueRequest();

		joinTimer.stop(Metrics.globalRegistry.timer("queue.wait.time"));
		return queueApplicationMapper.toQueueJoinResponseDto(storeId, userId, rank);
	}

	@Description("가게 대기열에서 본인 순서를 조회하는 메서드")
	public QueueUserRankResponseDto getRank(QueueUserSequenceServiceDto dto) {
		Long storeId = dto.storeId();
		Long userId = dto.userId();

		Integer rank = redisHelper.getUserQueuePosition(storeId, userId);

		if (rank == null) {
			throw new GlobalException(ErrorCode.WAITLIST_NOT_EXIST);
		}

		return queueApplicationMapper.toQueueUserRankResponseDto(storeId, userId, rank);
	}

	@Description("본인을 가게 대기열에서 삭제하는 메서드")
	@DistributedLock(key = "#dto.storeId")
	public QueueCancelResponseDto cancelQueue(QueueCancelServiceDto dto) {
		Long storeId = dto.storeId();
		Long userId = dto.userId();

		Integer sequence = redisHelper.getUserSequencePosition(storeId, userId);

		boolean removed = redisHelper.removeUserFromQueue(storeId, userId);

		if (!removed) {
			throw new GlobalException(ErrorCode.CAN_NOT_REMOVE_QUEUE);
		}

		Queue canceledQueue = Queue.cancelQueue(storeId, userId, sequence, CancelReason.USER_CANCEL);

		Queue cancelledUser = persistenceHelper.saveQueueInfo(canceledQueue);

		queueMetricsHelper.decrementWaitingUsers();
		return queueApplicationMapper.toQueueCancelResponseDto(cancelledUser.getStoreId(), cancelledUser.getUserId());
	}
}
