package com.boeingmerryho.business.storeservice.common;

import org.springframework.http.HttpStatus;

public interface SuccessCode {
	HttpStatus getStatus();

	String getMessage();
}