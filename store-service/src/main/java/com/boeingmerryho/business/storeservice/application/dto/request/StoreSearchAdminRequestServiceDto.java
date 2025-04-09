package com.boeingmerryho.business.storeservice.application.dto.request;

import org.springframework.data.domain.Pageable;

public record StoreSearchAdminRequestServiceDto(
	Pageable customPageable,
	Long stadiumId,
	String name,
	Boolean isClosed,
	Boolean isDeleted
) {
}
