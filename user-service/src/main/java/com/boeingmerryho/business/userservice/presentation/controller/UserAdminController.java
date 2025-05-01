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
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminEmailVerificationCheckRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminEmailVerificationRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminSearchRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminUpdateRoleRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserLogoutRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.response.admin.UserAdminFindResponseDto;
import com.boeingmerryho.business.userservice.application.service.UserAdminService;
import com.boeingmerryho.business.userservice.config.pageable.PageableConfig;
import com.boeingmerryho.business.userservice.presentation.UserSuccessCode;
import com.boeingmerryho.business.userservice.presentation.dto.mapper.UserPresentationMapper;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminEmailVerificationCheckRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminEmailVerificationRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminLoginRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminRegisterRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminSearchRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminTokenRefreshRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminUpdateRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.request.admin.UserAdminUpdateRoleRequestDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminLoginResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminRefreshTokenResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminSearchResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminUpdateRoleResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminVerificationResponseDto;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import jakarta.validation.Valid;
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
	public ResponseEntity<SuccessResponse<Long>> registerAdminUser(
		@RequestBody UserAdminRegisterRequestDto requestDto) {
		log.debug("[RegisterUser] Request received: {}", requestDto);
		UserAdminRegisterRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminSignUpServiceDto(
			requestDto);
		Long registeredUserId = userAdminService.registerUserAdmin(requestServiceDto);
		log.info("[RegisterUser] User registered: userId={}", registeredUserId);
		return SuccessResponse.of(UserSuccessCode.USER_REGISTER_SUCCESS, registeredUserId);
	}

	@Description("모든 사용자 리스트 중 id가 일치하는 사용자 조회")
	@GetMapping("/{id}")
	public ResponseEntity<SuccessResponse<UserAdminFindResponseDto>> findUser(@PathVariable Long id) {
		log.debug("[FindUser] Request received for userId: {}", id);
		UserAdminFindRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminFindRequestServiceDto(id);
		log.info("[FindUser] User found: {}", requestServiceDto.id());
		return SuccessResponse.of(UserSuccessCode.USER_FIND_SUCCESS, userAdminService.findUserAdmin(requestServiceDto));
	}

	@Description("업데이트 할 사용자 정보를 파라미터로 받아 정보 갱신")
	@PutMapping("/{id}")
	public ResponseEntity<SuccessResponse<UserAdminUpdateResponseDto>> updateUser(@PathVariable Long id,
		@RequestBody UserAdminUpdateRequestDto requestDto) {
		log.debug("[UpdateUser] Request received for userId: {}, data: {}", id, requestDto);
		UserAdminUpdateRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminUpdateRequestServiceDto(
			requestDto, id);
		UserAdminUpdateResponseDto responseDto = userAdminService.updateUser(requestServiceDto);
		log.info("[UpdateUser] User updated: {}", responseDto);
		return SuccessResponse.of(UserSuccessCode.USER_UPDATE_SUCCESS, responseDto);
	}

	@Description("본인 정보 수정")
	@PutMapping("/me")
	public ResponseEntity<?> updateUserMaster(@RequestAttribute("userId") Long id,
		@RequestBody UserAdminUpdateRequestDto requestDto) {
		log.debug("[UpdateUserMaster] Request received for userId: {}, data: {}", id, requestDto);
		UserAdminUpdateRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminUpdateRequestServiceDto(
			requestDto, id);
		UserAdminUpdateResponseDto responseDto = userAdminService.updateMe(requestServiceDto);
		log.info("[UpdateUserMaster] User updated: {}", responseDto);
		return SuccessResponse.of(UserSuccessCode.USER_UPDATE_SUCCESS, responseDto);
	}

	@Description("사용자 권한 변경")
	@PutMapping("/roles/{id}")
	public ResponseEntity<SuccessResponse<UserAdminUpdateRoleResponseDto>> updateRoleUserMaster(@PathVariable Long id,
		@RequestBody UserAdminUpdateRoleRequestDto requestDto) {
		log.debug("[UpdateRoleUserMaster] Request received for userId: {}, data: {}", id, requestDto);
		UserAdminUpdateRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminUpdateRoleRequestServiceDto(
			requestDto, id);
		UserAdminUpdateRoleResponseDto responseDto = userAdminService.updateUserRole(requestServiceDto);
		log.info("[UpdateRoleUserMaster] User role updated: {}", responseDto);
		return SuccessResponse.of(UserSuccessCode.USER_UPDATE_SUCCESS, responseDto);
	}

	@Description("회원 탈퇴(본인)")
	@DeleteMapping("/me")
	public ResponseEntity<?> withdrawUserMaster(@RequestAttribute("userId") Long id) {
		log.info("[WithdrawUserMaster] Request received for userId: {}", id);
		UserAdminWithdrawRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminWithdrawRequestServiceDto(
			id);
		Long withdrawId = userAdminService.withdrawUser(requestServiceDto);
		log.info("[WithdrawUserMaster] User withdrawn: {}", withdrawId);
		return SuccessResponse.of(UserSuccessCode.USER_WITHDRAW_SUCCESS, withdrawId);
	}

	@Description("사용자 삭제")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUserMaster(@RequestAttribute("userId") Long id) {
		log.info("[DeleteUserMaster] Request received for userId: {}", id);
		UserAdminDeleteRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminDeleteRequestServiceDto(
			id);
		Long deletedId = userAdminService.deleteUser(requestServiceDto);
		log.info("[DeleteUserMaster] User deleted: {}", deletedId);
		return SuccessResponse.of(UserSuccessCode.USER_DELETE_SUCCESS, deletedId);
	}

	@Description("사용자 권한 회수(삭제)")
	@DeleteMapping("/roles/{id}")
	public ResponseEntity<?> deleteRoleUserMaster(@PathVariable Long id) {
		log.info("[DeleteRoleUserMaster] Request received for userId: {}", id);
		UserAdminDeleteRoleRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminDeleteRoleRequestServiceDto(
			id);
		Long deletedId = userAdminService.deleteUserRole(requestServiceDto);
		log.info("[DeleteRoleUserMaster] User role deleted: {}", deletedId);
		return SuccessResponse.of(UserSuccessCode.USER_ROLE_DELETED_SUCCESS, deletedId);
	}

	@Description("사용자 정보를 선택적으로 파라미터로 받아 검색하는 api")
	@GetMapping("/search")
	public ResponseEntity<SuccessResponse<Page<UserAdminSearchResponseDto>>> searchUsers(
		@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
		@RequestParam(value = "size", required = false) Integer size,
		@RequestParam(value = "sortDirection", required = false) String sortDirection,
		@RequestParam(value = "by", required = false) String by, @ModelAttribute UserAdminSearchRequestDto requestDto) {
		log.debug("[SearchUsers] Request received with params: page={}, size={}, sortDirection={}, by={}",
			page, size, sortDirection, by);
		Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection, by);

		UserAdminSearchRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminSearchRequestServiceDto(
			requestDto, customPageable);
		Page<UserAdminSearchResponseDto> responseDtos = userAdminService.searchUsers(requestServiceDto, customPageable);
		log.info("[SearchUsers] Users found: {}", responseDtos.getContent());
		return SuccessResponse.of(UserSuccessCode.USER_SEARCH_SUCCESS, responseDtos);
	}

	@Description("사용자 email 중복 체크 api")
	@GetMapping("/check")
	public ResponseEntity<SuccessResponse<UserAdminCheckEmailResponseDto>> checkEmail(
		@RequestParam(value = "email") String email) {
		log.info("[CheckEmail] Request received for email: {}", email);
		UserAdminCheckEmailRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminCheckEmailRequestServiceDto(
			email);
		UserAdminCheckEmailResponseDto responseDto = userAdminService.checkEmail(requestServiceDto);
		log.info("[CheckEmail] Email check response: {}", responseDto);
		return SuccessResponse.of(UserSuccessCode.USER_EMAIL_CHECK_SUCCESS, responseDto);
	}

	@Description("사용자 email 인증 발송 요청 api")
	@PostMapping("/verify/send")
	public ResponseEntity<SuccessResponse<UserAdminVerificationResponseDto>> sendVerificationCode(
		@RequestBody @Valid UserAdminEmailVerificationRequestDto dto) {
		log.info("[SendVerificationCode] Request received: {}", dto);
		UserAdminEmailVerificationRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminEmailVerificationRequestServiceDto(
			dto);
		UserAdminVerificationResponseDto responseDto = userAdminService.sendVerificationCode(requestServiceDto);
		log.info("[SendVerificationCode] Verification code sent: {}", responseDto);
		return SuccessResponse.of(UserSuccessCode.VERIFICATION_EMAIL_SEND_SUCCESS, responseDto);
	}

	@Description("사용자 email 인증 api")
	@PostMapping("/verify/check")
	public ResponseEntity<SuccessResponse<UserAdminVerificationResponseDto>> checkVerificationCode(
		@RequestBody @Valid UserAdminEmailVerificationCheckRequestDto dto) {
		log.info("[CheckVerificationCode] Request received: {}", dto);
		UserAdminEmailVerificationCheckRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminEmailVerificationCheckRequestServiceDto(
			dto);
		UserAdminVerificationResponseDto responseDto = userAdminService.verifyCode(requestServiceDto);
		log.info("[CheckVerificationCode] Verification code checked: {}", responseDto);
		return SuccessResponse.of(UserSuccessCode.USER_EMAIL_VERIFICATION_SUCCESS, responseDto);
	}

	@Description("사용자 리프레시 토큰 재발급 api")
	@PostMapping("/refresh")
	public ResponseEntity<SuccessResponse<UserAdminRefreshTokenResponseDto>> refreshToken(
		@RequestBody UserAdminTokenRefreshRequestDto requestDto) {
		log.info("[RefreshToken] Request received: {}", requestDto);
		UserAdminRefreshTokenRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminRefreshTokenRequestServiceDto(
			requestDto);
		UserAdminRefreshTokenResponseDto responseDto = userAdminService.refreshToken(requestServiceDto);
		log.info("[RefreshToken] Token refreshed: {}", responseDto);
		return SuccessResponse.of(UserSuccessCode.USER_TOKEN_ISSUE_SUCCESS, responseDto);
	}

	@Description("email, password를 입력받아 로그인")
	@PostMapping("/login")
	public ResponseEntity<SuccessResponse<UserAdminLoginResponseDto>> loginUser(
		@RequestBody UserAdminLoginRequestDto requestDto) {
		log.info("[LoginUser] Request received: {}", requestDto);
		UserAdminLoginRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminLoginRequestServiceDto(
			requestDto);
		UserAdminLoginResponseDto responseDto = userAdminService.loginUserAdmin(requestServiceDto);
		log.info("[LoginUser] User logged in: {}", responseDto);
		return SuccessResponse.of(UserSuccessCode.USER_LOGIN_SUCCESS, responseDto);
	}

	@Description("로그인했던 사용자 id를 받아 로그아웃")
	@PostMapping("/logout")
	public ResponseEntity<?> logoutUser(
		@RequestAttribute("userId") Long userId
	) {
		log.info("[LogoutUser] Request received for userId: {}", userId);
		UserLogoutRequestServiceDto requestServiceDto = userPresentationMapper.toUserAdminLogoutRequestServiceDto(
			userId);
		userAdminService.logoutUser(requestServiceDto);
		log.info("[LogoutUser] User logged out: {}", userId);
		return SuccessResponse.of(UserSuccessCode.USER_LOGOUT_SUCCESS);
	}

}
