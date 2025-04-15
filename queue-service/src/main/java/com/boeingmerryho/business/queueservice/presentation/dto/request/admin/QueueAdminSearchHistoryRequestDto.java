package com.boeingmerryho.business.queueservice.presentation.dto.request.admin;

import java.util.Date;

import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.queueservice.domain.model.CancelReason;
import com.boeingmerryho.business.queueservice.domain.model.QueueStatus;

public record QueueAdminSearchHistoryRequestDto(
	Long storeId,
	Long userId,
	QueueStatus status,
	Date startDate,
	CancelReason cancelReason,
	Date endDate,
	Pageable pageable
) {
}
