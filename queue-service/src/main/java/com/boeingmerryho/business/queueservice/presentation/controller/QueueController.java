package com.boeingmerryho.business.queueservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueCancelServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueJoinServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueUserSequenceServiceDto;
import com.boeingmerryho.business.queueservice.application.service.QueueService;
import com.boeingmerryho.business.queueservice.presentation.QueueSuccessCode;
import com.boeingmerryho.business.queueservice.presentation.dto.mapper.QueuePresentationMapper;
import com.boeingmerryho.business.queueservice.presentation.dto.request.other.QueueJoinRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.request.other.QueueJoinResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueCancelResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueUserSequenceResponseDto;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/queues")
public class QueueController {

	private final QueueService queueService;
	private final QueuePresentationMapper queuePresentationMapper;

	@Description(
		"대기열 등록 api"
	)
	@PostMapping("/join")
	public ResponseEntity<?> joinQueue(
		@RequestAttribute Long userId,
		@RequestBody QueueJoinRequestDto requestDto) {
		QueueJoinServiceDto serviceDto = queuePresentationMapper.toQueueJoinRequestServiceDto(requestDto, userId);
		QueueJoinResponseDto sequence = queueService.joinQueue(serviceDto);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_JOIN_SUCCESS, sequence);
	}

	@Description(
		"대기열에서 본인 순서 조회 api"
	)
	@GetMapping("/me")
	public ResponseEntity<?> getMySequence(
		@RequestAttribute Long userId,
		@RequestParam Long storeId) {
		QueueUserSequenceServiceDto serviceDto = queuePresentationMapper.toQueueUserSequenceServiceDto(storeId, userId);
		QueueUserSequenceResponseDto sequence = queueService.getSequence(serviceDto);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_GET_SEQUENCE_SUCCESS, sequence);
	}

	@Description(
		"대기열에서 본인 순서 조회 api"
	)
	@DeleteMapping("/stores/{id}")
	public ResponseEntity<?> cancelQueue(
		@PathVariable(name = "id") Long storeId,
		@RequestAttribute Long userId
	) {
		QueueCancelServiceDto serviceDto = queuePresentationMapper.toQueueCancelServiceDto(storeId, userId);
		QueueCancelResponseDto sequence = queueService.cancelQueue(serviceDto);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_CANCEL_SUCCESS, sequence);
	}

}
