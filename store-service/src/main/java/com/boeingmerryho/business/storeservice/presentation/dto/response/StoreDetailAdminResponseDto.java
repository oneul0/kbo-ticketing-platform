package com.boeingmerryho.business.storeservice.presentation.dto.response;

import java.time.LocalDateTime;

public record StoreDetailAdminResponseDto(
	Long id,
	Long stadiumId,
	String name,
	LocalDateTime openAt,
	LocalDateTime closedAt,
	Boolean isClosed,
	Boolean isDeleted,
	Boolean isQueueAvailable
) {
}
