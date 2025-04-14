package com.boeingmerryho.business.queueservice.application.dto.mapper;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminCallUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminDeleteUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminItemListResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueCancelResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueJoinResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.other.QueueUserRankResponseDto;

@Mapper(componentModel = "spring")
public interface QueueApplicationMapper {

	QueueJoinResponseDto toQueueJoinResponseDto(Long storeId, Long userId, Integer sequence);

	QueueUserRankResponseDto toQueueUserRankResponseDto(Long storeId, Long userId, Integer rank);

	QueueCancelResponseDto toQueueCancelResponseDto(Long storeId, Long userId);

	QueueAdminDeleteUserResponseDto toQueueAdminDeleteUserResponseDto(Long storeId, Long userId);

	QueueAdminCallUserResponseDto toQueueAdminCallUserResponseDto(Long storeId, Long userId, Integer sequence);

	QueueAdminItemListResponseDto toQueueAdminItemListResponseDto(Long userId, Integer sequence);
}
