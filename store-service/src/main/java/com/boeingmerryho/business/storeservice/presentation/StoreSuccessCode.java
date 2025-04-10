package com.boeingmerryho.business.storeservice.presentation;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.response.SuccessCode;

public enum StoreSuccessCode implements SuccessCode {
	CREATED_STORE("매장이 성공적으로 등록되었습니다.", HttpStatus.CREATED),
	UPDATED_STORE("매장이 성공적으로 수정되었습니다.", HttpStatus.OK),
	FETCHED_STORE("매장 정보를 불러왔습니다.", HttpStatus.OK),
	FETCHED_STORES("매장 정보들을 불러왔습니다.", HttpStatus.OK),
	OPEN_STORE("매장이 오픈되었습니다.", HttpStatus.OK),
	CLOSE_STORE("매장이 마감되었습니다.", HttpStatus.OK),
	;

	private final String message;
	private final HttpStatus status;

	StoreSuccessCode(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

}