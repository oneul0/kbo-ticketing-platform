package com.boeingmerryho.business.queueservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminCallUserServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminDeleteUserServiceDto;
import com.boeingmerryho.business.queueservice.application.service.QueueAdminService;
import com.boeingmerryho.business.queueservice.config.pageable.PageableConfig;
import com.boeingmerryho.business.queueservice.presentation.QueueSuccessCode;
import com.boeingmerryho.business.queueservice.presentation.dto.mapper.QueuePresentationMapper;
import com.boeingmerryho.business.queueservice.presentation.dto.request.admin.QueueAdminCallUserRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.request.admin.QueueAdminQueueListRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminCallUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminDeleteUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminItemListResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminSearchResponseDto;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/queues")
public class QueueAdminController {

	private final PageableConfig pageableConfig;
	private final QueueAdminService queueAdminService;
	private final QueuePresentationMapper queuePresentationMapper;

	@Description(
		"대기열 강제 취소 api. manager, admin 사용 가능"
	)
	@DeleteMapping("/stores/{id}")
	public ResponseEntity<?> deleteUserFromQueue(
		@PathVariable(name = "id") Long storeId,
		@RequestParam Long userId) {
		QueueAdminDeleteUserServiceDto serviceDto = queuePresentationMapper.toQueueAdminDeleteUserServiceDto(storeId,
			userId);
		QueueAdminDeleteUserResponseDto sequence = queueAdminService.deleteUserFromQueue(serviceDto);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_DELETE_USER_SUCCESS, sequence);
	}

	@Description(
		"대기열의 다음 사용자 호출 api. manager 사용 가능"
	)
	@PostMapping("/call")
	public ResponseEntity<?> callNextUserFromQueue(
		@RequestBody QueueAdminCallUserRequestDto requestDto) {
		QueueAdminCallUserServiceDto serviceDto = queuePresentationMapper.toQueueAdminCallUserServiceDto(requestDto);
		QueueAdminCallUserResponseDto sequence = queueAdminService.callNextUserFromQueue(serviceDto);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_CALL_SUCCESS, sequence);
	}

	@Description(
		"가게의 대기열을 조회하는 api. manager, admin 사용 가능"
	)
	@GetMapping("/stores/{id}/queues")
	public ResponseEntity<QueueAdminSearchResponseDto> getQueueList(
		@PathVariable(name = "id") Long storeId,
		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
		@RequestParam(value = "size", required = false) Integer size
	) {
		Pageable customPageable = pageableConfig.customPageable(page, size, null, null);
		QueueAdminQueueListRequestDto requestDto = queuePresentationMapper.toQueueAdminQueueListRequestDto(storeId,
			customPageable);
		Page<QueueAdminItemListResponseDto> queuePageDto = queueAdminService.getQueueList(requestDto);
		return ResponseEntity.ok(new QueueAdminSearchResponseDto(storeId, queuePageDto));
	}
}
