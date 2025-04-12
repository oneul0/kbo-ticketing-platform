package com.boeingmerryho.business.queueservice.presentation.dto.response.admin;

public record QueueAdminStatusResponseDto(
	int queueNumber,
	boolean isCalled
) {
}
