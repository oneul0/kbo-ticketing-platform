package com.boeingmerryho.business.userservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.userservice.application.dto.request.other.UserCheckEmailRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserEmailVerificationCheckRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserEmailVerificationRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserLogoutRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.application.service.UserService;
import com.boeingmerryho.business.userservice.presentation.UserSuccessCode;
import com.boeingmerryho.business.userservice.presentation.dto.mapper.UserPresentationMapper;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserEmailVerificationCheckRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserEmailVerificationRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserLoginRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserRegisterRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserTokenRefreshRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.other.UserUpdateRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserFindResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserLoginResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserRefreshTokenResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserVerificationResponseDto;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserService userService;
	private final UserPresentationMapper userPresentationMapper;

	@Description(
		"username, password, slackId를 입력 받아 회원가입"
	)
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequestDto requestDto) {
		log.debug("[RegisterUser] Request received: {}", requestDto);
		UserRegisterRequestServiceDto requestServiceDto = userPresentationMapper.toUserRegisterRequestServiceDto(
			requestDto);
		Long registeredUserId = userService.registerUser(requestServiceDto);
		log.info("[RegisterUser] User registered: userId={}", registeredUserId);
		return SuccessResponse.of(UserSuccessCode.USER_REGISTER_SUCCESS, registeredUserId);
	}

	@GetMapping("/me")
	@Description(
		"user id로 본인 id 조회"
	)
	public ResponseEntity<SuccessResponse<UserFindResponseDto>> findUser(
		@RequestAttribute("userId") Long userId
	) {
		log.debug("[FindUser] Request received: userId={}", userId);
		UserFindRequestServiceDto requestServiceDto = userPresentationMapper.toUserSearchRequestServiceDto(userId);
		UserFindResponseDto responseDto = userService.findUser(requestServiceDto);
		log.info("[FindUser] User found: {}", responseDto);
		return SuccessResponse.of(UserSuccessCode.USER_FIND_SUCCESS, responseDto);
	}

	@Description(
		"본인 정보 수정"
	)
	@PutMapping("/me")
	public ResponseEntity<?> updateUser(@RequestAttribute("userId") Long id,
		@RequestBody UserUpdateRequestDto requestDto) {
		log.debug("[UpdateUser] Request received: userId={}, updateData={}", id, requestDto);
		UserUpdateRequestServiceDto requestServiceDto = userPresentationMapper.toUserUpdateRequestServiceDto(
			requestDto, id);
		UserAdminUpdateResponseDto responseDto = userService.updateMe(requestServiceDto);
		log.info("[UpdateUser] User updated: {}", responseDto);
		return SuccessResponse.of(UserSuccessCode.USER_UPDATE_SUCCESS, responseDto);
	}

	@Description(
		"회원 탈퇴"
	)
	@DeleteMapping("/me")
	public ResponseEntity<?> withdrawUser(@RequestAttribute("userId") Long id) {
		log.debug("[WithdrawUser] Request received: userId={}", id);
		UserWithdrawRequestServiceDto requestServiceDto = userPresentationMapper.toUserWithdrawRequestServiceDto(
			id);
		Long withdrawUserId = userService.withdrawUser(requestServiceDto);
		log.info("[WithdrawUser] User withdrawn: userId={}", withdrawUserId);
		return SuccessResponse.of(UserSuccessCode.USER_WITHDRAW_SUCCESS, withdrawUserId);
	}

	@Description(
		"username, password를 입력받아 로그인"
	)
	@PostMapping("/login")
	public ResponseEntity<SuccessResponse<UserLoginResponseDto>> loginUser(
		@RequestBody UserLoginRequestDto requestDto) {
		log.info("[LoginUser] Login attempt: email={}", requestDto.email());
		UserLoginRequestServiceDto requestServiceDto = userPresentationMapper.toUserLoginRequestServiceDto(
			requestDto);
		UserLoginResponseDto responseDto = userService.loginUser(requestServiceDto);
		log.info("[LoginUser] Login successful: accessToken issued");
		return SuccessResponse.of(UserSuccessCode.USER_LOGIN_SUCCESS, responseDto);
	}

	@PostMapping("/logout")
	@Description(
		"로그인했던 사용자 id를 받아 로그아웃"
	)
	public ResponseEntity<?> logoutUser(
		@RequestAttribute("userId") Long userId
	) {
		log.info("[LogoutUser] Logout request received: userId={}", userId);
		UserLogoutRequestServiceDto requestServiceDto = userPresentationMapper.toUserLogoutRequestServiceDto(
			userId);
		userService.logoutUser(requestServiceDto);
		log.info("[LogoutUser] Logout successful: userId={}", userId);
		return SuccessResponse.of(UserSuccessCode.USER_LOGOUT_SUCCESS);
	}

	@Description(
		"사용자 email 중복 체크 api"
	)
	@GetMapping("/check")
	public ResponseEntity<SuccessResponse<UserCheckEmailResponseDto>> checkEmail(
		@RequestParam(value = "email") String email) {
		log.info("[CheckEmail] Email check request: email={}", email);
		UserCheckEmailRequestServiceDto requestServiceDto = userPresentationMapper.toUserCheckEmailRequestServiceDto(
			email);
		UserCheckEmailResponseDto responseDto = userService.checkEmail(
			requestServiceDto);
		log.info("[CheckEmail] Email check result: {}", responseDto);
		return SuccessResponse.of(UserSuccessCode.USER_EMAIL_CHECK_SUCCESS, responseDto);
	}

	@Description("사용자 email 인증 발송 요청 api")
	@PostMapping("/verify/send")
	public ResponseEntity<SuccessResponse<UserVerificationResponseDto>> sendVerificationCode(
		@RequestBody @Valid UserEmailVerificationRequestDto dto) {
		log.info("[SendVerificationCode] Sending verification code: email={}", dto.email());
		UserEmailVerificationRequestServiceDto requestServiceDto = userPresentationMapper.toUserEmailVerificationRequestServiceDto(
			dto);
		UserVerificationResponseDto responseDto = userService.sendVerificationCode(requestServiceDto);
		log.info("[SendVerificationCode] Verification code sent");
		return SuccessResponse.of(UserSuccessCode.VERIFICATION_EMAIL_SEND_SUCCESS, responseDto);
	}

	@Description("사용자 email 인증 api")
	@PostMapping("/verify/check")
	public ResponseEntity<SuccessResponse<UserVerificationResponseDto>> checkVerificationCode(
		@RequestBody @Valid UserEmailVerificationCheckRequestDto dto) {
		log.info("[CheckVerificationCode] Checking code for email={}", dto.email());
		UserEmailVerificationCheckRequestServiceDto requestServiceDto = userPresentationMapper.toUserEmailVerificationCheckRequestServiceDto(
			dto);
		UserVerificationResponseDto responseDto = userService.verifyCode(requestServiceDto);
		log.info("[CheckVerificationCode] Verification code checked successfully");
		return SuccessResponse.of(UserSuccessCode.USER_EMAIL_VERIFICATION_SUCCESS, responseDto);
	}

	@Description(
		"사용자 리프레시 토큰 재발급 api"
	)
	@PostMapping("/refresh")
	public ResponseEntity<SuccessResponse<UserRefreshTokenResponseDto>> refreshToken(
		@RequestBody UserTokenRefreshRequestDto requestDto) {
		log.info("[RefreshToken] Refresh token requested");
		UserRefreshTokenRequestServiceDto requestServiceDto = userPresentationMapper.toUserRefreshTokenRequestServiceDto(
			requestDto);
		UserRefreshTokenResponseDto responseDto = userService.refreshToken(requestServiceDto);
		log.info("[RefreshToken] Token refreshed successfully");
		return SuccessResponse.of(UserSuccessCode.USER_TOKEN_ISSUE_SUCCESS, responseDto);
	}
}
