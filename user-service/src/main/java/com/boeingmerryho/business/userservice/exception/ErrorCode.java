package com.boeingmerryho.business.userservice.exception;

import org.springframework.http.HttpStatus;

import io.github.boeingmerryho.commonlibrary.exception.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements BaseErrorCode {

	NOT_FOUND("U-001", "해당 유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
	USERNAME_NULL("U-002", "Username이 비어 있습니다.", HttpStatus.BAD_REQUEST),
	PASSWORD_NULL("U-003", "Password가 비어 있습니다.", HttpStatus.BAD_REQUEST),
	EMAIL_NULL("U-004", "Email이 비어 있습니다.", HttpStatus.BAD_REQUEST),
	ALREADY_EXISTS("U-005", "이미 존재하는 사용자입니다.", HttpStatus.BAD_REQUEST),
	USER_NOT_MATCH("U-006", "요청한 사용자와 정보가 다릅니다.", HttpStatus.BAD_REQUEST),

	CANNOT_GRANT_MASTER_ROLE("U-012", "ADMIN 권한은 부여할 수 없습니다.", HttpStatus.BAD_REQUEST),
	LESS_ROLE("U-013", "사용자의 권한이 부족합니다.", HttpStatus.BAD_REQUEST),
	ADMIN_REGISTER_KEY_IS_NULL("U-014", "ADMIN 인증을 위한 key값이 비어 있습니다.", HttpStatus.BAD_REQUEST),
	USERNAME_REGEX_NOT_MATCH("U-015", "사용자 이름은 4~10자이며, 소문자(a-z)와 숫자(0-9)만 사용할 수 있습니다.", HttpStatus.BAD_REQUEST),
	PASSWORD_REGEX_NOT_MATCH("U-016", "비밀번호는 8~15자이며, 대소문자(A-Z, a-z), 숫자(0-9), 특수문자(!@#$%^&*)를 포함해야 합니다.",
		HttpStatus.BAD_REQUEST),
	ADMIN_REGISTER_KEY_NOT_MATCH("U-017", "ADMIN 인증을 위한 key값이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),

	ROLE_ACTIVE_USERS_NOT_FOUND("U-018", "Role에 따른 활성화된 사용자가 없습니다.", HttpStatus.NOT_FOUND),

	JWT_REQUIRED("U-019", "로그아웃을 위해 JWT 토큰이 필요합니다.", HttpStatus.UNAUTHORIZED),
	JWT_NOT_FOUND("U-020", "로그인시 생성한 JWT 토큰이 없습니다.", HttpStatus.UNAUTHORIZED),
	JWT_INVALID("U-021", "JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
	JWT_NOT_MATCH("U-022", "JWT 토큰이 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
	JWT_EXPIRED("U-023", "JWT 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
	MALFORMED_JWT("U-024", "JWT 토큰이 손상되었습니다.", HttpStatus.UNAUTHORIZED),
	JWT_SIGNATURE_VALIDATION_FAIL("U-025", "JWT 검증에 실패했습니다.", HttpStatus.UNAUTHORIZED),

	VERIFICATION_ALREADY_SENT("U-026", "이미 요청한 내역이 있습니다.", HttpStatus.UNAUTHORIZED),
	VERIFICATION_FAIL("U-027", "인증 코드가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

	EMAIL_SEND_FAILED("U-028", "인증 코드를 위한 이메일 발송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

	MEMBERSHIP_INFO_SETTING_FAIL("U-029", "멤버십 정보를 저장하는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	MEMBERSHIP_FEIGN_REQUEST_FAIL("U-030", "Membership service feign요청에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

	LOGIN_FAILED("U-031", "로그인에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
	;

	private final String errorCode;
	private final String message;
	private final HttpStatus status;
}
