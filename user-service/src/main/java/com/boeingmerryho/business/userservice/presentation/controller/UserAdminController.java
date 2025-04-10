package com.boeingmerryho.business.userservice.presentation.controller;

import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminCheckEmailRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminDeleteRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminDeleteRoleRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminSearchRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminUpdateRoleRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserLogoutRequestServiceDto;
import com.boeingmerryho.business.userservice.application.service.UserAdminService;
import com.boeingmerryho.business.userservice.config.pageable.PageableConfig;
import com.boeingmerryho.business.userservice.presentation.dto.mapper.UserPresentationMapper;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminLoginRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminRegisterRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminSearchRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminTokenRefreshRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminUpdateRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminUpdateRoleRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminRefreshTokenResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminSearchResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminUpdateRoleResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserLoginResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/users")
public class UserAdminController {

	private final UserAdminService userAdminService;
	private final UserPresentationMapper userPresentationMapper;
	private final PageableConfig pageableConfig;

	@Description("username, password, email, key를 입력 받아 회원가입")
	@PostMapping("/register")
	public ResponseEntity<Long> registerAdminUser(@RequestBody UserAdminRegisterRequestDto requestDto) {
		UserAdminRegisterRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminSignUpServiceDto(
			requestDto);
		Long registeredUserId = userAdminService.registerUserAdmin(requestServiceDto);
		return ResponseEntity.ok().body(registeredUserId);
	}

	@Description("모든 사용자 리스트 중 id가 일치하는 사용자 조회")
	@GetMapping("/{id}")
	public ResponseEntity<?> findUser(@PathVariable Long id) {
		UserAdminFindRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminFindRequestServiceDto(id);
		return ResponseEntity.ok(userAdminService.findUserAdmin(requestServiceDto));
	}

	@Description("업데이트 할 사용자 정보를 파라미터로 받아 정보 갱신")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserAdminUpdateRequestDto requestDto) {
		UserAdminUpdateRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminUpdateRequestServiceDto(
			requestDto, id);
		UserAdminUpdateResponseDto responseDto = userAdminService.updateUser(requestServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description("본인 정보 수정")
	@PutMapping("/me")
	public ResponseEntity<?> updateUserMaster(@RequestAttribute("userId") Long id,
		@RequestBody UserAdminUpdateRequestDto requestDto) {
		UserAdminUpdateRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminUpdateRequestServiceDto(
			requestDto, id);
		UserAdminUpdateResponseDto responseDto = userAdminService.updateMe(requestServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description("사용자 권한 변경")
	@PutMapping("/roles/{id}")
	public ResponseEntity<UserAdminUpdateRoleResponseDto> updateRoleUserMaster(@PathVariable Long id,
		@RequestBody UserAdminUpdateRoleRequestDto requestDto) {
		UserAdminUpdateRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminUpdateRoleRequestServiceDto(
			requestDto, id);
		UserAdminUpdateRoleResponseDto responseDto = userAdminService.updateUserRole(requestServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description("사용자 삭제")
	@DeleteMapping("/me")
	public ResponseEntity<?> withdrawUserMaster(@PathVariable Long id) {
		UserAdminWithdrawRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminWithdrawRequestServiceDto(
			id);
		userAdminService.withdrawUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@Description("회원 탈퇴(본인)")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUserMaster(@RequestAttribute("userId") Long id) {
		UserAdminDeleteRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminDeleteRequestServiceDto(
			id);
		userAdminService.deleteUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@Description("사용자 권한 회수(삭제)")
	@DeleteMapping("/roles/{id}")
	public ResponseEntity<?> deleteRoleUserMaster(@PathVariable Long id) {
		UserAdminDeleteRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminDeleteRoleRequestServiceDto(
			id);
		userAdminService.deleteUserRole(requestServiceDto);
		return ResponseEntity.ok().build();
	}

	@Description("사용자 정보를 선택적으로 파라미터로 받아 검색하는 api")
	@GetMapping("/search")
	public ResponseEntity<Page<UserAdminSearchResponseDto>> searchUsers(
		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
		@RequestParam(value = "size", required = false) Integer size,
		@RequestParam(value = "sortDirection", required = false) String sortDirection,
		@RequestParam(value = "by", required = false) String by, @ModelAttribute UserAdminSearchRequestDto requestDto) {

		Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection, by);

		UserAdminSearchRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminSearchRequestServiceDto(
			requestDto, customPageable);
		Page<UserAdminSearchResponseDto> responseDtos = userAdminService.searchUsers(requestServiceDto, customPageable);
		return ResponseEntity.ok(responseDtos);
	}

	@Description("사용자 email 중복 체크 api")
	@GetMapping("/check")
	public ResponseEntity<UserAdminCheckEmailResponseDto> checkEmail(@RequestParam(value = "email") String email) {

		UserAdminCheckEmailRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminCheckEmailRequestServiceDto(
			email);
		UserAdminCheckEmailResponseDto responseDto = userAdminService.checkEmail(requestServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description("사용자 리프레시 토큰 재발급 api")
	@PostMapping("/refresh")
	public ResponseEntity<UserAdminRefreshTokenResponseDto> refreshToken(
		@RequestBody UserAdminTokenRefreshRequestDto requestDto) {
		log.info("refreshed token : {}", requestDto.refreshToken());
		UserAdminRefreshTokenRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminRefreshTokenRequestServiceDto(
			requestDto);
		UserAdminRefreshTokenResponseDto responseDto = userAdminService.refreshToken(requestServiceDto);
		return ResponseEntity.ok(responseDto);
	}

	@Description("email, password를 입력받아 로그인")
	@PostMapping("/login")
	public ResponseEntity<UserLoginResponseDto> loginUser(@RequestBody UserAdminLoginRequestDto requestDto) {
		UserAdminLoginRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminLoginRequestServiceDto(
			requestDto);
		UserLoginResponseDto responseDto = userAdminService.loginUserAdmin(requestServiceDto);
		return ResponseEntity.ok().body(responseDto);
	}

	@Description("로그인했던 사용자 id를 받아 로그아웃")
	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser(@RequestAttribute("userId") Long userId) {
		UserLogoutRequestServiceDto requestServiceDto = userPresentationMapper.toUserLogoutRequestServiceDto(userId);
		userAdminService.logoutUser(requestServiceDto);
		return ResponseEntity.ok().build();
	}

}
