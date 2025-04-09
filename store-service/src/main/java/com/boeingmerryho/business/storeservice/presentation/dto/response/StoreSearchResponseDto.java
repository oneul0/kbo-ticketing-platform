package com.boeingmerryho.business.storeservice.presentation.dto.response;

import java.time.LocalDateTime;

public record StoreSearchResponseDto(
	Long id,
	Long stadiumId,
	String name,
	LocalDateTime openAt,
	LocalDateTime closedAt,
	Boolean isClosed
) {
}