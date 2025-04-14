package com.boeingmerryho.business.queueservice.presentation.dto.response.admin;

import com.boeingmerryho.business.queueservice.domain.model.CancelReason;
import com.boeingmerryho.business.queueservice.domain.model.QueueStatus;

public record QueueAdminSearchHistoryResponseDto(
	Long id,
	Long storeId,
	Long userId,
	Integer sequence,
	QueueStatus status,
	CancelReason cancelReason
) {
}
