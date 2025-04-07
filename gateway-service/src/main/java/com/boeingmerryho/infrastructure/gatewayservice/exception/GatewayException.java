package com.boeingmerryho.infrastructure.gatewayservice.exception;


import lombok.Getter;

@Getter
public class GatewayException extends RuntimeException{

	private final ErrorCode errorCode;

	public GatewayException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
