package com.boeingmerryho.business.queueservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminCallUserServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminDeleteHistoryServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminDeleteUserServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminSearchHistoryServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminUpdateHistoryServiceDto;
import com.boeingmerryho.business.queueservice.application.service.QueueAdminService;
import com.boeingmerryho.business.queueservice.config.pageable.PageableConfig;
import com.boeingmerryho.business.queueservice.presentation.QueueSuccessCode;
import com.boeingmerryho.business.queueservice.presentation.dto.mapper.QueuePresentationMapper;
import com.boeingmerryho.business.queueservice.presentation.dto.request.admin.QueueAdminCallUserRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.request.admin.QueueAdminQueueListRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.request.admin.QueueAdminSearchHistoryRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminCallUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminDeleteUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminHistoryListResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminSearchHistoryResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminSearchResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminUpdateHistoryRequestDto;

import io.github.boeingmerryho.commonlibrary.entity.UserRoleType;
import io.github.boeingmerryho.commonlibrary.interceptor.RequiredRoles;
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

	@Description("대기열 강제 취소 api. manager, admin 사용 가능")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	@DeleteMapping("/stores/{id}")
	public ResponseEntity<?> deleteUserFromQueue(
		@PathVariable(name = "id") Long storeId,
		@RequestParam Long userId
	) {
		QueueAdminDeleteUserServiceDto serviceDto = queuePresentationMapper.toQueueAdminDeleteUserServiceDto(storeId,
			userId);
		QueueAdminDeleteUserResponseDto sequence = queueAdminService.deleteUserFromQueue(serviceDto);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_DELETE_USER_SUCCESS, sequence);
	}

	@Description("대기열의 다음 사용자 호출 api. manager 사용 가능")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	@PostMapping("/call")
	public ResponseEntity<?> callNextUserFromQueue(@RequestBody QueueAdminCallUserRequestDto requestDto) {
		QueueAdminCallUserServiceDto serviceDto = queuePresentationMapper.toQueueAdminCallUserServiceDto(requestDto);
		QueueAdminCallUserResponseDto sequence = queueAdminService.callNextUserFromQueue(serviceDto);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_CALL_SUCCESS, sequence);
	}

	@Description("가게의 대기열을 조회하는 api. manager, admin 사용 가능")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	@GetMapping("/stores/{id}/queues")
	public ResponseEntity<SuccessResponse<QueueAdminSearchResponseDto>> getQueueList(
		@PathVariable(name = "id") Long storeId,
		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
		@RequestParam(value = "size", required = false) Integer size) {
		Pageable customPageable = pageableConfig.customPageable(page, size, null, null);
		QueueAdminQueueListRequestDto requestDto = queuePresentationMapper.toQueueAdminQueueListRequestDto(storeId,
			customPageable);
		Page<QueueAdminHistoryListResponseDto> queuePageDto = queueAdminService.getQueueList(requestDto);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_SEARCH_STATUS_SUCCESS,
			new QueueAdminSearchResponseDto(storeId, queuePageDto));
	}

	@Description("가게의 대기열 기록을 조회하는 api. manager, admin 사용 가능")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	@GetMapping("/stores/history")
	public ResponseEntity<SuccessResponse<Page<QueueAdminSearchHistoryResponseDto>>> getQueueHistory(
		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
		@RequestParam(value = "size", required = false) Integer size,
		@RequestParam(value = "sortDirection", required = false) String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@ModelAttribute QueueAdminSearchHistoryRequestDto requestDto) {
		Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection, by);

		QueueAdminSearchHistoryServiceDto serviceDto = queuePresentationMapper.toQueueAdminSearchHistoryServiceDto(
			requestDto, customPageable);
		Page<QueueAdminSearchHistoryResponseDto> responseDto = queueAdminService.getQueueHistory(serviceDto);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_HISTORY_SEARCH_SUCCESS, responseDto);
	}

	@Description("가게의 대기열 기록을 삭제하는 api. manager, admin 사용 가능")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	@DeleteMapping("/stores/history/{id}")
	public ResponseEntity<SuccessResponse<Long>> deleteQueueHistory(
		@PathVariable(name = "id") Long id,
		@RequestAttribute Long userId
	) {

		QueueAdminDeleteHistoryServiceDto serviceDto = queuePresentationMapper.toQueueAdminDeleteHistoryServiceDto(id,
			userId);
		Long deletedId = queueAdminService.deleteQueueHistory(serviceDto);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_HISTORY_DELETE_SUCCESS, deletedId);
	}

	@Description("가게의 대기열 기록을 수정하는 api. manager, admin 사용 가능")
	@RequiredRoles({UserRoleType.ADMIN, UserRoleType.MANAGER})
	@PutMapping("/stores/history/{id}")
	public ResponseEntity<SuccessResponse<Long>> updateQueueHistory(
		@PathVariable(name = "id") Long id,
		@RequestAttribute Long userId,
		@RequestBody QueueAdminUpdateHistoryRequestDto requestDto) {

		QueueAdminUpdateHistoryServiceDto serviceDto = queuePresentationMapper.toQueueAdminUpdateHistoryServiceDto(
			requestDto, id, userId);
		Long updatedId = queueAdminService.updateQueueHistory(serviceDto);
		return SuccessResponse.of(QueueSuccessCode.QUEUE_HISTORY_UPDATE_SUCCESS, updatedId);
	}
}
