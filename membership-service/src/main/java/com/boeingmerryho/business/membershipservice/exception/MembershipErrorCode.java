package com.boeingmerryho.business.membershipservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;

public enum MembershipErrorCode implements BaseErrorCode {

	BAD_REQUEST(HttpStatus.BAD_REQUEST, "MEMBERSHIP_000", "잘못된 요청입니다."),
	ALREADY_REGISTERED(HttpStatus.CONFLICT, "MEMBERSHIP_001", "이미 등록된 멤버십입니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBERSHIP_002", "존재하지 않는 멤버십입니다."),
	NO_UPDATE_FIELDS_PROVIDED(HttpStatus.BAD_REQUEST, "MEMBERSHIP_003", "필드 값이 유효하지 않습니다."),
	INVALID_MEMBERSHIP_TYPE(HttpStatus.BAD_REQUEST, "MEMBERSHIP_004", "정의되지 않은 멤버십 타입입니다."),
	OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "MEMBERSHIP_005", "해당 멤버십은 재고가 소진되었습니다."),
	ALREADY_RESERVED(HttpStatus.CONFLICT, "MEMBERSHIP_006", "이미 멤버십을 선점한 사용자입니다."),
	REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBERSHIP_007", "Redis 처리 중 오류가 발생했습니다."),
	UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBERSHIP_008", "알 수 없는 서버 오류가 발생했습니다."),
	PAYMENT_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "MEMBERSHIP_009", "결제 요청에 실패했습니다."),
	NOT_FOUND_MEMBERSHIP(HttpStatus.NOT_FOUND, "MEMBERSHIP_010", "멤버십 정보가 조회되지 않습니다."),
	;

	private final HttpStatus status;
	private final String errorCode;
	private final String message;

	MembershipErrorCode(HttpStatus status, String errorCode, String message) {
		this.status = status;
		this.errorCode = errorCode;
		this.message = message;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
