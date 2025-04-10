package com.boeingmerryho.business.userservice.application.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.userservice.application.UserHelper;
import com.boeingmerryho.business.userservice.application.dto.mapper.UserApplicationMapper;
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
import com.boeingmerryho.business.userservice.application.dto.response.admin.UserAdminFindResponseDto;
import com.boeingmerryho.business.userservice.application.dto.response.inner.UserTokenResult;
import com.boeingmerryho.business.userservice.application.dto.response.other.UserLoginResponseServiceDto;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.domain.UserSearchCriteria;
import com.boeingmerryho.business.userservice.domain.repository.CustomUserRepository;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminRefreshTokenResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminSearchResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.admin.UserAdminUpdateRoleResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.other.UserLoginResponseDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminService {

	private final UserRepository userRepository;
	private final CustomUserRepository customUserRepository;
	private final UserApplicationMapper userApplicationMapper;
	private final PasswordEncoder passwordEncoder;

	private final UserHelper userHelper;

	@Value("${admin.key}")
	private String adminKey;

	@Transactional
	public Long registerUserAdmin(UserAdminRegisterRequestServiceDto dto) {
		validateAdminKey(dto.adminKey());
		userHelper.validateRegisterRequest(dto, userRepository);

		User user = User.withAdminRole(
			dto.username(),
			userHelper.encodePassword(dto.password(), passwordEncoder),
			dto.email(),
			dto.nickname(),
			dto.birth()
		);

		return userRepository.save(user).getId();
	}

	private void validateAdminKey(String key) {
		if (key == null || !key.equals(adminKey)) {
			throw new GlobalException(ErrorCode.ADMIN_REGISTER_KEY_NOT_MATCH);
		}
	}

	@Transactional(readOnly = true)
	public UserAdminCheckEmailResponseDto checkEmail(UserAdminCheckEmailRequestServiceDto dto) {
		Boolean isEmailDuplicated = userRepository.existsByEmail(dto.email());
		return userApplicationMapper.toUserAdminCheckEmailResponseDto(isEmailDuplicated);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "user", key = "#dto.id()")
	public UserAdminFindResponseDto findUserAdmin(UserAdminFindRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id(), userRepository);
		return userApplicationMapper.toUserAdminFindResponseDto(user);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "users")
	public Page<UserAdminSearchResponseDto> searchAdminUsers(UserAdminSearchRequestServiceDto dto, Pageable pageable) {
		UserSearchCriteria criteria = UserSearchCriteria.fromAdmin(dto);
		Page<User> users = customUserRepository.findDynamicQuery(criteria, pageable);
		return users.map(userApplicationMapper::toUserAdminSearchResponseDto);
	}

	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "users")
	public Page<UserAdminSearchResponseDto> searchUsers(UserAdminSearchRequestServiceDto dto, Pageable pageable) {
		UserSearchCriteria criteria = UserSearchCriteria.fromAdmin(dto); // 일반 검색 DTO 사용
		Page<User> users = customUserRepository.findDynamicQuery(criteria, pageable);
		return users.map(userApplicationMapper::toUserAdminSearchResponseDto);
	}

	@Transactional
	public UserAdminUpdateRoleResponseDto updateUserRole(UserAdminUpdateRoleRequestServiceDto dto) {
		userHelper.isAdminRole(dto.newRole());

		User user = userHelper.findUserById(dto.id(), userRepository);

		UserRoleType oldRole = user.getRole();
		user.updateRoleType(dto.newRole());
		userHelper.updateRedisUserInfo(user);

		return userApplicationMapper.toUserAdminUpdateRoleResponseDto(dto.id(), oldRole, dto.newRole());
	}

	@Transactional
	public Long deleteUserRole(UserAdminDeleteRoleRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id(), userRepository);

		user.deleteRoleType();
		userHelper.updateRedisUserInfo(user);
		return user.getId();
	}

	@Transactional
	public Long deleteUser(UserAdminDeleteRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id(), userRepository);
		user.softDelete(user.getId());

		userHelper.clearRedisUserData(user.getId());
		return user.getId();
	}

	public void logoutUser(UserLogoutRequestServiceDto dto) {
		Long userId = dto.id();

		UserTokenResult result = userHelper.getUserTokenFromRedis(userId);
		String accessToken = (String)result.token().get("accessToken");
		userHelper.blacklistToken(accessToken);

		userHelper.deleteKeyFromRedis(result.tokenKey());
	}

	public UserLoginResponseDto loginUserAdmin(UserAdminLoginRequestServiceDto dto) {
		User user = userHelper.findUserByEmail(dto.email(), userRepository);
		userHelper.updateRedisUserInfo(user);

		Map<String, String> tokenMap = userHelper.updateUserJwtTokenRedis(user.getId());
		UserLoginResponseServiceDto serviceDto = UserLoginResponseServiceDto.fromTokens(
			tokenMap.get("accessToken"),
			tokenMap.get("refreshToken")
		);
		return userApplicationMapper.toUserLoginResponseDto(serviceDto);
	}

	@Transactional
	public Long withdrawUser(UserAdminWithdrawRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id(), userRepository);
		user.softDelete(user.getId());

		userHelper.clearRedisUserData(user.getId());
		return user.getId();
	}

	@Transactional
	public UserAdminRefreshTokenResponseDto refreshToken(UserAdminRefreshTokenRequestServiceDto dto) {
		String refreshToken = dto.refreshToken();

		log.debug("refresh requested refreshToken : {}", refreshToken);
		userHelper.isValidRefreshToken(refreshToken);

		Long userId = userHelper.getUserIdFromToken(refreshToken);

		userHelper.isEqualStoredRefreshToken(userId, refreshToken);

		String newAccessToken = userHelper.generateAccessToken(userId);

		return new UserAdminRefreshTokenResponseDto(newAccessToken);
	}

	@Transactional
	public UserAdminUpdateResponseDto updateUser(UserAdminUpdateRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id(), userRepository);

		if (!userHelper.isEmpty(dto.password())) {
			user.updatePassword(passwordEncoder.encode(dto.password()));
		}
		if (!userHelper.isEmpty(dto.username())) {
			user.updateUsername(dto.username());
		}
		if (!userHelper.isEmpty(dto.nickname())) {
			user.updateNickname(dto.nickname());
		}
		if (dto.birth() != null) {
			user.updateBirth(dto.birth());
		}

		userRepository.save(user);

		return new UserAdminUpdateResponseDto(user.getId());
	}

	@Transactional
	public UserAdminUpdateResponseDto updateMe(UserAdminUpdateRequestServiceDto dto) {

		User user = userHelper.findUserById(dto.id(), userRepository);

		if (!userHelper.isEmpty(dto.password())) {
			String encodedPassword = userHelper.encodePassword(dto.password(), passwordEncoder);
			user.updatePassword(encodedPassword);
		}
		if (!userHelper.isEmpty(dto.username())) {
			user.updateUsername(dto.username());
		}
		if (!userHelper.isEmpty(dto.nickname())) {
			user.updateNickname(dto.nickname());
		}
		if (dto.birth() != null) {
			user.updateBirth(dto.birth());
		}

		userHelper.updateRedisUserInfo(user);

		return userApplicationMapper.toUserAdminUpdateResponseDto(user.getId());
	}
}