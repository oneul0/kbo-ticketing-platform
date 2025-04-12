package com.boeingmerryho.business.queueservice.application.service;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.queueservice.application.dto.mapper.QueueApplicationMapper;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueCancelServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueJoinServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueUserSequenceServiceDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueJoinResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueCancelResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueUserSequenceResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

	private final QueueApplicationMapper queueApplicationMapper;

	@Description("대기열에 등록하는 메서드")
	public QueueJoinResponseDto joinQueue(QueueJoinServiceDto serviceDto) {
		Long storeId = 0L;
		Integer sequence = 0;

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
