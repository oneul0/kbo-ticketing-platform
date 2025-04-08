package com.boeingmerryho.business.paymentservice.presentation.temp;

import org.springframework.http.HttpStatus;

public interface SuccessCode {
	HttpStatus getStatus();

	String getMessage();
}
