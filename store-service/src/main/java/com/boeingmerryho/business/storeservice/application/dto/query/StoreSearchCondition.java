package com.boeingmerryho.business.storeservice.application.dto.query;

public record StoreSearchCondition(
	Long stadiumId,
	String name,
	Boolean isClosed,
	Boolean isDeleted
) {
}