package com.boeingmerryho.business.queueservice.application.service;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.queueservice.application.QueueHelper;
import com.boeingmerryho.business.queueservice.application.dto.mapper.QueueApplicationMapper;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminCallUserServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminDeleteUserServiceDto;
import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.domain.model.CancelReason;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;
import com.boeingmerryho.business.queueservice.presentation.dto.request.admin.QueueAdminQueueListRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminCallUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminDeleteUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminItemListResponseDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueAdminService {

	private final QueueApplicationMapper queueApplicationMapper;
	private final QueueHelper helper;

	@Description("대기열에서 사용자 강제 삭제 메서드")
	public QueueAdminDeleteUserResponseDto deleteUserFromQueue(QueueAdminDeleteUserServiceDto dto) {
		Long storeId = dto.storeId();
		Long userId = dto.userId();

		Integer sequence = helper.getUserSequencePosition(storeId, userId);

		boolean removed = helper.removeUserFromQueue(storeId, userId);

		if (!removed) {
			throw new GlobalException(ErrorCode.CAN_NOT_REMOVE_QUEUE);
		}

		Queue canceledQueue = Queue.cancelQueue(storeId, userId, sequence, CancelReason.ILLEGAL_USED); //todo: 삭제 이유 세분화

		Queue cancelledUser = helper.saveQueueInfo(canceledQueue);

		return queueApplicationMapper.toQueueAdminDeleteUserResponseDto(cancelledUser.getStoreId(),
			cancelledUser.getUserId());
	}

	@Description("대기열의 다음 사용자 호출 메서드")
	public QueueAdminCallUserResponseDto callNextUserFromQueue(QueueAdminCallUserServiceDto dto) {
		Long storeId = 0L;
		Long userId = 0L;
		Integer sequence = 0;
		return queueApplicationMapper.toQueueAdminCallUserResponseDto(storeId, userId, sequence);
	}

	@Description("가게의 대기열 정보를 가져오는 메서드")
	public Page<QueueAdminItemListResponseDto> getQueueList(QueueAdminQueueListRequestDto dto) {
		return null;
	}
}
