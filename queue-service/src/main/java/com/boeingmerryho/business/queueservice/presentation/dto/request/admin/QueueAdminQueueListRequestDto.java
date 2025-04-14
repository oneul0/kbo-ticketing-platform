package com.boeingmerryho.business.queueservice.presentation.dto.request.admin;

import org.springframework.data.domain.Pageable;

public record QueueAdminQueueListRequestDto(Long storeId, Pageable pageable) {
}
