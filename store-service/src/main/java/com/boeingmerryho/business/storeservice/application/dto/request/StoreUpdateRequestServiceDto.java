package com.boeingmerryho.business.storeservice.application.dto.request;

import java.time.LocalDateTime;

public record StoreUpdateRequestServiceDto(
	String name,
	LocalDateTime openAt,
	LocalDateTime closedAt
) {
}
