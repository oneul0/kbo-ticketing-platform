package com.boeingmerryho.business.queueservice.application.dto.mapper;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.queueservice.presentation.dto.request.other.QueueJoinResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueCancelResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueUserSequenceResponseDto;

@Mapper(componentModel = "spring")
public interface QueueApplicationMapper {

	QueueJoinResponseDto toQueueJoinResponseDto(Long userId, Integer sequence);

	QueueUserSequenceResponseDto toQueueUserSequenceResponseDto(Long storeId, Long userId, Integer sequence);

	QueueCancelResponseDto toQueueCancelResponseDto(Long storeId, Long userId);
}
