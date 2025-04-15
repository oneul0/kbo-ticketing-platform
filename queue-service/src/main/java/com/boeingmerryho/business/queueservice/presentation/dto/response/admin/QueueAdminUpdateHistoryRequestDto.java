package com.boeingmerryho.business.queueservice.presentation.dto.response.admin;

import com.boeingmerryho.business.queueservice.domain.model.CancelReason;
import com.boeingmerryho.business.queueservice.domain.model.QueueStatus;

public record QueueAdminUpdateHistoryRequestDto(
	Long storeId,
	Long userId,
	QueueStatus status,
	CancelReason cancelReason
) {
}
