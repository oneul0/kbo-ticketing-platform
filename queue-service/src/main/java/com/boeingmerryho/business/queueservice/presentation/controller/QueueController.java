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
import com.boeingmerryho.business.queueservice.exception.ErrorCode;
import com.boeingmerryho.business.queueservice.presentation.QueueSuccessCode;
import com.boeingmerryho.business.queueservice.presentation.dto.mapper.QueuePresentationMapper;
import com.boeingmerryho.business.queueservice.presentation.dto.request.other.QueueJoinRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueCancelResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueJoinResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueUserRankResponseDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
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

		log.info("[Join Queue] request userId: {}, storeId: {}", userId, requestDto.storeId());

		QueueJoinServiceDto serviceDto = queuePresentationMapper.toQueueJoinServiceDto(requestDto, userId);
		QueueJoinResponseDto rank = null;
		try {
			rank = queueService.joinQueue(serviceDto);
		} catch (InterruptedException e) {
			throw new GlobalException(ErrorCode.LOCK_ACQUISITION_FAIL);
		}
		log.info("[Join Queue] complete userId: {}, storeId: {}, rank: {}", userId, requestDto.storeId(), rank);

		return SuccessResponse.of(QueueSuccessCode.QUEUE_JOIN_SUCCESS, rank);
	}

	@Description(
		"대기열에서 본인 순서 조회 api"
	)
	@GetMapping("/me")
	public ResponseEntity<?> getMySequence(
		@RequestAttribute Long userId,
		@RequestParam Long storeId) {
		log.info("[Get My Sequence] request userId: {}, storeId: {}", userId, storeId);

		QueueUserSequenceServiceDto serviceDto = queuePresentationMapper.toQueueUserSequenceServiceDto(storeId, userId);
		QueueUserRankResponseDto rank = queueService.getRank(serviceDto);

		log.info("[Get My Sequence] complete userId: {}, storeId: {}, rank: {}", userId, storeId, rank);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_GET_SEQUENCE_SUCCESS, rank);
	}

	@Description(
		"대기열 취소 api"
	)
	@DeleteMapping("/stores/{id}")
	public ResponseEntity<?> cancelQueue(
		@RequestAttribute Long userId,
		@PathVariable(name = "id") Long storeId
	) {
		log.info("[Cancel Queue] request userId: {}, storeId: {}", userId, storeId);

		QueueCancelServiceDto serviceDto = queuePresentationMapper.toQueueCancelServiceDto(storeId, userId);
		QueueCancelResponseDto sequence = queueService.cancelQueue(serviceDto);

		log.info("[Cancel Queue] complete userId: {}, storeId: {}", userId, storeId);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_CANCEL_SUCCESS, sequence);
	}

}
