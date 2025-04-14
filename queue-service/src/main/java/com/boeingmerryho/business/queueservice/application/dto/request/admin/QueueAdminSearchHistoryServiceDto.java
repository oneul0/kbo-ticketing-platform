package com.boeingmerryho.business.queueservice.application.dto.request.admin;

import java.util.Date;

import org.springframework.data.domain.Pageable;

public record QueueAdminSearchHistoryServiceDto(Long storeId, Date startDate, Date endDate, Pageable pageable) {
}
