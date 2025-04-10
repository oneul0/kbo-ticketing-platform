package com.boeingmerryho.business.storeservice.application.dto.response;

import java.time.LocalDateTime;

public record StoreDetailAdminResponseServiceDto(
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
