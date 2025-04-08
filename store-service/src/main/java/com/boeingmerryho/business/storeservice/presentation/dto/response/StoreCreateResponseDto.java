package com.boeingmerryho.business.storeservice.presentation.dto.response;

import java.time.LocalDateTime;

public record StoreCreateResponseDto(
	Long id,
	Long stadiumId,
	String name,
	LocalDateTime openAt,
	LocalDateTime closedAt,
	Boolean isClosed
) {
}
