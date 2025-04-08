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

import com.boeingmerryho.business.userservice.application.dto.request.UserCheckEmailRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserLogoutRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.application.service.UserService;
import com.boeingmerryho.business.userservice.presentation.dto.mapper.UserPresentationMapper;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserLoginRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserRegisterRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.UserUpdateRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserFindResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserLoginResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserRefreshTokenResponseDto;

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
		UserRegisterRequestServiceDto requestServiceDto = userPresentationMapper.toUserRegisterRequestServiceDto(
			requestDto);
		userService.registerUser(requestServiceDto);
		//todo: 반환값 반환하기
		return ResponseEntity.ok().build();
	}

	@GetMapping("/me")
	@Description(
		"user id로 본인 id 조회"
	)
	public ResponseEntity<UserFindResponseDto> findUser(
		@RequestAttribute("userId") Long userId
	) {
		log.debug("search userId:{}", userId);
		UserFindRequestServiceDto requestServiceDto = userPresentationMapper.toUserSearchRequestServiceDto(userId);
		UserFindResponseDto responseDto = userService.findUser(requestServiceDto);
		log.debug(responseDto.toString());
		return ResponseEntity.ok(responseDto);
	}

	@Description(
		"본인 정보 수정"
	)
	@PutMapping("/me")
	public ResponseEntity<?> updateUser(@RequestAttribute("userId") Long id,
		@RequestBody UserUpdateRequestDto requestDto) {
		UserUpdateRequestServiceDto requestServiceDto = userPresentationMapper.toUserUpdateRequestServiceDto(
			requestDto, id);
		UserAdminUpdateResponseDto responseDto = userService.updateUser(requestServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description(
		"회원 탈퇴"
	)
	@DeleteMapping("/me")
	public ResponseEntity<?> withdrawUser(@RequestAttribute("userId") Long id) {
		UserWithdrawRequestServiceDto requestServiceDto = userPresentationMapper.toUserWithdrawRequestServiceDto(
			id);
		userService.deleteUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@Description(
		"username, password를 입력받아 로그인"
	)
	@PostMapping("/login")
	public ResponseEntity<UserLoginResponseDto> loginUser(
		@RequestBody UserLoginRequestDto requestDto) {
		UserLoginRequestServiceDto requestServiceDto = userPresentationMapper.toUserLoginRequestServiceDto(
			requestDto);
		UserLoginResponseDto responseDto = userService.loginUser(requestServiceDto);
		return ResponseEntity.ok().body(responseDto);
	}

	@PostMapping("/logout")
	@Description(
		"로그인했던 사용자 id를 받아 로그아웃"
	)
	public ResponseEntity<?> logoutUser(
		@RequestAttribute("userId") Long userId
	) {
		UserLogoutRequestServiceDto requestServiceDto = userPresentationMapper.toUserLogoutRequestServiceDto(userId);
		userService.logoutUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@Description(
		"사용자 email 중복 체크 api"
	)
	@GetMapping("/check")
	public ResponseEntity<UserCheckEmailResponseDto> checkEmail(
		@RequestParam(value = "email") String email) {

		UserCheckEmailRequestServiceDto requestServiceDto = userPresentationMapper.toUserCheckEmailRequestServiceDto(
			email);
		UserCheckEmailResponseDto responseDto = userService.checkEmail(
			requestServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description(
		"사용자 리프레시 토큰 재발급 api"
	)
	@GetMapping("/refresh")
	public ResponseEntity<UserRefreshTokenResponseDto> refreshToken(
		@RequestParam(value = "refreshToken") String refreshToken) {

		UserRefreshTokenRequestServiceDto requestServiceDto = userPresentationMapper.toUserRefreshTokenRequestServiceDto(
			refreshToken);
		UserRefreshTokenResponseDto responseDto = userService.refreshToken(requestServiceDto);
		return ResponseEntity.ok(responseDto);
	}
}
