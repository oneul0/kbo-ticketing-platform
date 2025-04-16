package com.boeingmerryho.business.queueservice.application.dto.request.admin;

import com.boeingmerryho.business.queueservice.domain.model.CancelReason;
import com.boeingmerryho.business.queueservice.domain.model.QueueStatus;

public record QueueAdminUpdateHistoryServiceDto(
	Long id,
	Long storeId,
	Long userId,
	QueueStatus status,
	CancelReason cancelReason
) {
}
