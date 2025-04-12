package com.boeingmerryho.business.queueservice.presentation.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueCancelServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueJoinServiceDto;
import com.boeingmerryho.business.queueservice.application.dto.request.other.QueueUserSequenceServiceDto;
import com.boeingmerryho.business.queueservice.presentation.dto.request.other.QueueJoinRequestDto;

@Mapper(componentModel = "spring")
public interface QueuePresentationMapper {

	@Mapping(target = "userId", source = "userId")
	QueueJoinServiceDto toQueueJoinRequestServiceDto(
		QueueJoinRequestDto requestDto, Long userId);

	@Mapping(target = "storeId", source = "storeId")
	@Mapping(target = "userId", source = "userId")
	QueueUserSequenceServiceDto toQueueUserSequenceServiceDto(Long storeId, Long userId);

	QueueCancelServiceDto toQueueCancelServiceDto(Long storeId, Long userId);
}
