package com.boeingmerryho.business.queueservice.presentation.dto.response.admin;

import org.springframework.data.domain.Page;

import com.boeingmerryho.business.queueservice.domain.model.CancelReason;
import com.boeingmerryho.business.queueservice.domain.model.QueueStatus;

public record QueueAdminSearchHistoryResponseDto(
	Long id,
	Long storeId,
	Integer sequence,
	QueueStatus status,
	CancelReason cancelReason,
	Page<QueueAdminSearchHistoryResponseDto> queuePageDto
) {
}
