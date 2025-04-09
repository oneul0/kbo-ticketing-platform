package com.boeingmerryho.business.paymentservice.exception;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;
import io.github.boeingmerryho.commonlibrary.exception.GlobalException;

public class PaymentException extends GlobalException {
	public PaymentException(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
