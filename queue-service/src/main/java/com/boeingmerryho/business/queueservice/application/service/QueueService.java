package com.boeingmerryho.business.queueservice.application.service;

import java.util.Date;
import java.util.Objects;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.queueservice.application.QueueHelper;
import com.boeingmerryho.business.queueservice.application.dto.mapper.QueueApplicationMapper;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueCancelServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueJoinServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueUserSequenceServiceDto;
import com.boeingmerryho.business.queueservice.config.aop.DistributedLock;
import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.domain.model.CancelReason;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueCancelResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueJoinResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueUserSequenceResponseDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final QueueApplicationMapper queueApplicationMapper;
	private final QueueHelper helper;

	@Description("대기열에 등록하는 메서드")
	@DistributedLock(key = "#dto.storeId")
	public QueueJoinResponseDto joinQueue(QueueJoinServiceDto dto) throws InterruptedException {
		Long storeId = dto.storeId();
		Long userId = dto.userId();
		Long ticketId = dto.ticketId();
		Date matchDate = new Date();

		Long ticketUserId = helper.validateTicket(matchDate, ticketId);
		if (!Objects.equals(userId, ticketUserId)) {
			throw new GlobalException(ErrorCode.USER_IS_NOT_MATCHED);
		}

		if (!helper.validateStoreIsActive(storeId)) {
			throw new GlobalException(ErrorCode.STORE_IS_NOT_ACTIVATED);
		}

		helper.joinUserInQueue(storeId, userId, ticketId);

		Integer sequence = helper.getUserQueuePosition(storeId, userId);

		return queueApplicationMapper.toQueueJoinResponseDto(storeId, userId, sequence);
	}

	@Description("가게 대기열에서 본인 순서를 조회하는 메서드")
	public QueueUserSequenceResponseDto getSequence(QueueUserSequenceServiceDto serviceDto) {
		Long storeId = serviceDto.storeId();
		Long userId = serviceDto.userId();

		Integer sequence = helper.getUserQueuePosition(storeId, userId);

		if (sequence == null) {
			throw new GlobalException(ErrorCode.WAITLIST_NOT_EXIST);
		}

		return queueApplicationMapper.toQueueUserSequenceResponseDto(storeId, userId, sequence);
	}

	@Description("본인을 가게 대기열에서 삭제하는 메서드")
	public QueueCancelResponseDto cancelQueue(QueueCancelServiceDto serviceDto) {
		Long storeId = serviceDto.storeId();
		Long userId = serviceDto.userId();

		Integer sequence = helper.getUserQueuePosition(storeId, userId);

		boolean removed = helper.removeUserFromQueue(storeId, userId);

		if (!removed) {
			throw new GlobalException(ErrorCode.CAN_NOT_REMOVE_QUEUE);
		}

		Queue canceledQueue = Queue.cancelQueue(storeId, userId, sequence, CancelReason.USER_CANCEL);

		Queue cancelledUser = helper.saveQueueInfo(canceledQueue);

		return queueApplicationMapper.toQueueCancelResponseDto(cancelledUser.getStoreId(), cancelledUser.getUserId());
	}
}
