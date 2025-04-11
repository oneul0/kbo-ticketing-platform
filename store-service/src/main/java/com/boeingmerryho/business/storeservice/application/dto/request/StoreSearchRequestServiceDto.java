package com.boeingmerryho.business.storeservice.application.dto.request;

import org.springframework.data.domain.Pageable;

public record StoreSearchRequestServiceDto(
	Pageable customPageable,
	Long stadiumId,
	String name,
	Boolean isClosed
) {
}