package com.boeingmerryho.business.userservice.application.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.userservice.application.UserHelper;
import com.boeingmerryho.business.userservice.application.dto.mapper.UserApplicationMapper;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminCheckEmailRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminDeleteRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminDeleteRoleRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminFindRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminLoginRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminRefreshTokenRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminSearchRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminUpdateRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminUpdateRoleRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserAdminWithdrawRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserLogoutRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.response.UserAdminFindResponseDto;
import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.application.utils.jwt.JwtTokenProvider;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.domain.UserSearchCriteria;
import com.boeingmerryho.business.userservice.domain.repository.CustomUserRepository;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;
import com.boeingmerryho.business.userservice.exception.UserException;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminCheckEmailResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminRefreshTokenResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminSearchResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminUpdateResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserAdminUpdateRoleResponseDto;
import com.boeingmerryho.business.userservice.presentation.dto.response.UserLoginResponseDto;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAdminService {

	private static final String USER_INFO_PREFIX = "user:info:";
	private static final String USER_TOKEN_PREFIX = "user:token:";
	private static final String BLACKLIST_PREFIX = "blacklist:";

	private final UserRepository userRepository;
	private final CustomUserRepository customUserRepository;
	private final UserApplicationMapper userApplicationMapper;
	private final PasswordEncoder passwordEncoder;
	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisUtil redisUtil;
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
			throw new UserException(ErrorCode.ADMIN_REGISTER_KEY_NOT_MATCH);
		}
	}

	@Transactional(readOnly = true)
	@Cacheable
	public UserAdminCheckEmailResponseDto checkEmail(UserAdminCheckEmailRequestServiceDto dto) {
		throw new UnsupportedOperationException("Not implemented yet");
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
		User user = userHelper.findUserById(dto.id(), userRepository);
		userHelper.checkMasterRole(user);

		UserRoleType oldRole = user.getRole();
		user.updateRoleType(dto.newRole());
		userHelper.updateRedisUserInfo(user, redisUtil);

		// return UserAdminUpdateRoleResponseDto.of(dto.id(), oldRole, dto.newRole());
		return null;
	}

	@Transactional
	public void deleteUserRole(UserAdminDeleteRoleRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id(), userRepository);
		userHelper.checkMasterRole(user);

		user.deleteRoleType();
		userHelper.updateRedisUserInfo(user, redisUtil);
	}

	@Transactional
	public void deleteUser(UserAdminDeleteRequestServiceDto dto) {
		User user = userHelper.findUserById(dto.id(), userRepository);
		user.softDelete(user.getId());

		clearRedisUserData(user.getId());
	}

	private void clearRedisUserData(Long userId) {
		redisTemplate.delete(USER_INFO_PREFIX + userId);
		redisTemplate.delete(USER_TOKEN_PREFIX + userId);
	}

	public void logoutUser(UserLogoutRequestServiceDto dto) {
		Long userId = dto.id();
		String tokenKey = USER_TOKEN_PREFIX + userId;

		Map<Object, Object> token = getUserTokenFromRedis(tokenKey);
		String accessToken = extractAccessToken(token);
		blacklistToken(accessToken);

		redisTemplate.delete(tokenKey);
	}

	private Map<Object, Object> getUserTokenFromRedis(String tokenKey) {
		if (!redisTemplate.hasKey(tokenKey)) {
			throw new UserException(ErrorCode.NOT_FOUND);
		}
		Map<Object, Object> token = redisTemplate.opsForHash().entries(tokenKey);
		if (token == null || token.isEmpty()) {
			throw new UserException(ErrorCode.JWT_REQUIRED);
		}
		return token;
	}

	private String extractAccessToken(Map<Object, Object> token) {
		return (String)token.get("accessToken");
	}

	private void blacklistToken(String accessToken) {
		Claims claims = jwtTokenProvider.parseJwtToken(accessToken);
		long ttlMillis = jwtTokenProvider.calculateTtlMillis(claims.getExpiration());
		String blacklistKey = BLACKLIST_PREFIX + accessToken;

		redisTemplate.opsForValue().set(blacklistKey, "blacklisted");
		redisTemplate.expire(blacklistKey, Math.max(ttlMillis, 1), TimeUnit.MILLISECONDS);
	}

	public UserLoginResponseDto loginUserAdmin(UserAdminLoginRequestServiceDto dto) {
		// User user = userHelper.findUserByEmail(dto.email(), userRepository);
		// userHelper.updateRedisUserInfo(user, redisUtil);
		//
		// Map<String, String> tokenMap = redisUtil.updateUserJwtToken(user.getId());
		// UserLoginResponseServiceDto serviceDto = UserLoginResponseServiceDto.fromTokens(
		// 	tokenMap.get("accessToken"),
		// 	tokenMap.get("refreshToken")
		// );
		// return userApplicationMapper.toUserLoginResponseDto(serviceDto);
		return null;
	}

	@Transactional
	public void withdrawUser(UserAdminWithdrawRequestServiceDto dto) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	public UserAdminRefreshTokenResponseDto refreshToken(UserAdminRefreshTokenRequestServiceDto dto) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Transactional
	public UserAdminUpdateResponseDto updateUser(UserAdminUpdateRequestServiceDto dto) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Transactional
	public UserAdminUpdateResponseDto updateMe(UserAdminUpdateRequestServiceDto dto) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}