package com.boeingmerryho.business.queueservice.presentation.dto.response.admin;

import org.springframework.data.domain.Page;

public record QueueAdminSearchResponseDto(
	Long storeId,
	Page<QueueAdminHistoryListResponseDto> queuePageDto
) {
}
