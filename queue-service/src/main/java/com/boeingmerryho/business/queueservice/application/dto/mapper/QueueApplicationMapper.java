package com.boeingmerryho.business.queueservice.application.dto.mapper;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminCallUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminDeleteUserResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminHistoryListResponseDto;
import com.boeingmerryho.business.queueservice.presentation.dto.response.admin.QueueAdminSearchHistoryResponseDto;
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

	QueueAdminSearchHistoryResponseDto toQueueAdminSearchHistoryResponseDto(Queue queue);

	QueueAdminHistoryListResponseDto toQueueAdminItemListResponseDto(Long userId, Integer sequence);
}
