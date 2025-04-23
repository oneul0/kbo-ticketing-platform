package com.boeingmerryho.business.storeservice.application.service.command;

import com.boeingmerryho.business.storeservice.exception.StoreErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;

public enum StoreCommandType {
	OPEN,
	CLOSE,
	;

	public static StoreCommandType from(String value) {
		try {
			return StoreCommandType.valueOf(value.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new GlobalException(StoreErrorCode.INVALID_STATUS);
		}
	}

}
