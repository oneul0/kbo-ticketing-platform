package com.boeingmerryho.business.userservice.presentation;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.response.SuccessCode;

public enum UserSuccessCode implements SuccessCode {
	USER_REGISTER_SUCCESS("결제 내역을 조회했습니다.", HttpStatus.OK),
	USER_FIND_SUCCESS("조회에 성공했습니다.", HttpStatus.OK),
	USER_UPDATE_SUCCESS("사용자 정보 수정에 성공했습니다.", HttpStatus.OK),
	USER_DELETE_SUCCESS("사용자 삭제에 성공했습니다.", HttpStatus.OK),
	USER_WITHDRAW_SUCCESS("회원 탈퇴에 성공했습니다.", HttpStatus.OK),
	USER_ROLE_DELETED_SUCCESS("사용자의 ROLE을 삭제했습니다.", HttpStatus.OK),
	USER_SEARCH_SUCCESS("검색에 성공했습니다.", HttpStatus.OK),
	USER_EMAIL_CHECK_SUCCESS("검색에 성공했습니다.", HttpStatus.OK),

	USER_TOKEN_ISSUE_SUCCESS("사용자 ACCESS TOKEN발급에 성공했습니다.", HttpStatus.OK),

	USER_LOGIN_SUCCESS("로그인에 성공했습니다.", HttpStatus.OK),
	USER_LOGOUT_SUCCESS("로그아웃에 성공했습니다.", HttpStatus.OK),
	;

	private final String message;
	private final HttpStatus status;

	UserSuccessCode(String message, HttpStatus status) {
		this.message = message;
		this.status = status;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
