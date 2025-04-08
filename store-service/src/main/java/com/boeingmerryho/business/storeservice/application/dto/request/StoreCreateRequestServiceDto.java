package com.boeingmerryho.business.storeservice.application.dto.request;

import java.time.LocalDateTime;

public record StoreCreateRequestServiceDto(
	Long stadiumId,
	String name,
	LocalDateTime openAt,
	LocalDateTime closedAt,
	Boolean isClosed
) {
}
