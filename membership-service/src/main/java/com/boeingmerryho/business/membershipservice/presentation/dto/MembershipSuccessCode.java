package com.boeingmerryho.business.membershipservice.presentation.dto;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.response.SuccessCode;

public enum MembershipSuccessCode implements SuccessCode {
	CREATED_MEMBERSHIP("멤버십이 성공적으로 생성되었습니다.", HttpStatus.CREATED),
	FETCHED_MEMBERSHIP("멤버십이 성공적으로 조회되었습니다.", HttpStatus.OK),
	FETCHED_MEMBERSHIPS("멤버십 정보들을 불러왔습니다", HttpStatus.OK),
	UPDATED_STORE("멤버십이 성공적으로 수정되었습니다.", HttpStatus.OK),
	DELETE_MEMBERSHIP("멤버십이 성공적으로 삭제되었습니다.", HttpStatus.OK),
	CREATED_MEMBERSHIP_USER("멤버십 등록에 성공하였습니다.", HttpStatus.CREATED),
	;

	private final String message;
	private final HttpStatus status;

	MembershipSuccessCode(String message, HttpStatus status) {
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
