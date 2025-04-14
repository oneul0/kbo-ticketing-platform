package com.boeingmerryho.business.queueservice.presentation.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminCallUserServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminDeleteUserServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminSearchHistoryServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.admin.QueueAdminStatusServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueCancelServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueJoinServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueUserSequenceServiceDto;
import com.boeingmerryho.business.queueservice.presentation.dto.request.admin.QueueAdminCallUserRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.request.admin.QueueAdminQueueListRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.request.admin.QueueAdminSearchHistoryRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.request.admin.QueueAdminStatusRequestDto;
import com.boeingmerryho.business.queueservice.presentation.dto.request.other.QueueJoinRequestDto;

@Mapper(componentModel = "spring")
public interface QueuePresentationMapper {

	@Mapping(target = "userId", source = "userId")
		// QueueJoinServiceDto toQueueJoinServiceDto(QueueJoinRequestDto requestDto, Long userId);
	QueueJoinServiceDto toQueueJoinServiceDto(QueueJoinRequestDto requestDto);

	@Mapping(target = "storeId", source = "storeId")
	@Mapping(target = "userId", source = "userId")
	QueueUserSequenceServiceDto toQueueUserSequenceServiceDto(Long storeId, Long userId);

	QueueCancelServiceDto toQueueCancelServiceDto(Long storeId, Long userId);

	QueueAdminDeleteUserServiceDto toQueueAdminDeleteUserServiceDto(Long storeId, Long userId);

	@Mapping(target = "storeId", source = "storeId")
	QueueAdminCallUserServiceDto toQueueAdminCallUserServiceDto(QueueAdminCallUserRequestDto requestDto);

	QueueAdminStatusServiceDto toQueueAdminStatusServiceDto(QueueAdminStatusRequestDto requestDto);

	@Mapping(target = "storeId", source = "storeId")
	@Mapping(target = "pageable", source = "customPageable")
	QueueAdminQueueListRequestDto toQueueAdminQueueListRequestDto(Long storeId, Pageable customPageable);

	@Mapping(target = "pageable", source = "customPageable")
	QueueAdminSearchHistoryServiceDto toQueueAdminSearchHistoryServiceDto(QueueAdminSearchHistoryRequestDto requestDto,
		Pageable customPageable);
}
