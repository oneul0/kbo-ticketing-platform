package com.boeingmerryho.business.queueservice.application.service;

import java.util.Date;
import java.util.Objects;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.queueservice.application.QueueJoinHelper;
import com.boeingmerryho.business.queueservice.application.dto.mapper.QueueApplicationMapper;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueCancelServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueJoinServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueUserSequenceServiceDto;
import com.boeingmerryho.business.queueservice.exception.ErrorCode;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueJoinResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueCancelResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueUserSequenceResponseDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final QueueApplicationMapper queueApplicationMapper;
	private final QueueJoinHelper joinHelper;

	@Description("대기열에 등록하는 메서드")
	public QueueJoinResponseDto joinQueue(QueueJoinServiceDto dto) {
		Long storeId = dto.storeId();
		Long userId = dto.userId();
		Long ticketId = dto.ticketId();
		Date matchDate = new Date();

		Long ticketUserId = joinHelper.validateTicket(matchDate, ticketId);
		if(!Objects.equals(userId, ticketUserId)) {
			throw new GlobalException(ErrorCode.USER_IS_NOT_MATCHED);
		}

		if(!joinHelper.validateStoreIsActive(storeId)){
			throw new GlobalException(ErrorCode.STORE_IS_NOT_ACTIVATED);
		}

		joinHelper.joinUserInQueue(storeId, userId, ticketId);

		Integer sequence = joinHelper.getUserQueuePosition(storeId, userId);

		return queueApplicationMapper.toQueueJoinResponseDto(storeId, sequence);
	}

	@Description("가게 대기열에서 본인 순서를 조회하는 메서드")
	public QueueUserSequenceResponseDto getSequence(QueueUserSequenceServiceDto serviceDto) {
		Long storeId = 0L;
		Long userId = 0L;
		Integer sequence = 0;

		return queueApplicationMapper.toQueueUserSequenceResponseDto(storeId, userId, sequence);
	}

	@Description("본인을 가게 대기열에서 삭제하는 메서드")
	public QueueCancelResponseDto cancelQueue(QueueCancelServiceDto serviceDto) {
		Long storeId = 0L;
		Long userId = 0L;
		return queueApplicationMapper.toQueueCancelResponseDto(storeId, userId);
	}
}
