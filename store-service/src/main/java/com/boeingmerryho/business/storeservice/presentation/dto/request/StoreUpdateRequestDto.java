package com.boeingmerryho.business.storeservice.presentation.dto.request;

import java.time.LocalDateTime;

public record StoreUpdateRequestDto(
	String name,
	LocalDateTime openAt,
	LocalDateTime closedAt
) {
}
