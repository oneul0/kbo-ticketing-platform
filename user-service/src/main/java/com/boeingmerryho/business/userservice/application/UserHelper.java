package com.boeingmerryho.business.userservice.application;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.userservice.application.dto.request.UserAdminRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.application.utils.jwt.JwtTokenProvider;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;
import com.boeingmerryho.business.userservice.exception.UserException;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserHelper {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$");

	private static final String USER_INFO_PREFIX = "user:info:";
	private static final String USER_TOKEN_PREFIX = "user:token:";
	private static final String BLACKLIST_PREFIX = "blacklist:";

	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisUtil redisUtil;

	public User findUserById(Long id, UserRepository userRepository) {
		return userRepository.findById(id)
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));
	}

	public User findUserByEmail(String email, UserRepository userRepository) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));
	}

	public void validateRegisterRequest(UserAdminRegisterRequestServiceDto dto, UserRepository userRepository) {
		validateCommonFields(dto.email(), dto.password(), dto.username(), dto.nickname(), dto.birth());
		checkEmailExists(dto.email(), userRepository);
	}

	public void validateRegisterRequest(UserRegisterRequestServiceDto dto, UserRepository userRepository) {
		validateCommonFields(dto.email(), dto.password(), dto.username(), dto.nickname(), dto.birth());
		checkEmailExists(dto.email(), userRepository);
	}

	private void validateCommonFields(String email, String password, String username, String nickname,
		LocalDate birth) {
		validateRequiredField(email, ErrorCode.EMAIL_NULL);
		validateRequiredField(password, ErrorCode.PASSWORD_NULL);
		validateRequiredField(username, ErrorCode.USERNAME_NULL);
		validateRequiredField(nickname, ErrorCode.USERNAME_NULL);
		validateRequiredField(birth, ErrorCode.USERNAME_NULL);

		verifyEmailFormat(email);
		verifyPasswordFormat(password);
	}

	private void validateRequiredField(String field, ErrorCode errorCode) {
		if (isEmpty(field)) {
			throw new UserException(errorCode);
		}
	}

	private void validateRequiredField(LocalDate field, ErrorCode errorCode) {
		if (field == null) {
			throw new UserException(errorCode);
		}
	}

	public boolean isEmpty(String field) {
		return field == null || field.trim().isEmpty();
	}

	private void verifyEmailFormat(String email) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			throw new UserException(ErrorCode.USERNAME_REGEX_NOT_MATCH);
		}
	}

	private void verifyPasswordFormat(String password) {
		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			throw new UserException(ErrorCode.PASSWORD_REGEX_NOT_MATCH);
		}
	}

	public void checkEmailExists(String email, UserRepository userRepository) {
		if (userRepository.existsByEmail(email)) {
			throw new UserException(ErrorCode.ALREADY_EXISTS);
		}
	}

	public String encodePassword(String password, PasswordEncoder passwordEncoder) {
		return passwordEncoder.encode(password);
	}

	public void checkMasterRole(User user) {
		if (user.isAdmin()) {
			throw new UserException(ErrorCode.CANNOT_GRANT_MASTER_ROLE);
		}
	}

	public void isAdminRole(UserRoleType type) {
		if (type.equals(UserRoleType.ADMIN)) {
			throw new UserException(ErrorCode.CANNOT_GRANT_MASTER_ROLE);
		}
	}

	//-----redis

	public void updateRedisUserInfo(User user) {
		redisUtil.updateUserInfo(user);
	}

	public void clearRedisUserData(Long userId) {
		redisTemplate.delete(USER_INFO_PREFIX + userId);
		redisTemplate.delete(USER_TOKEN_PREFIX + userId);
	}

	public void deleteKeyFromRedis(String tokenKey) {
		redisTemplate.delete(tokenKey);
	}

	public void hasKeyInRedis(String key) {
		if (!redisTemplate.hasKey(key)) {
			throw new UserException(ErrorCode.NOT_FOUND);
		}
	}

	public Map<Object, Object> getMapEntriesFromRedis(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	public void setOpsForValueRedis(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public void blacklistToken(String accessToken) {
		Claims claims = jwtTokenProvider.parseJwtToken(accessToken);
		long ttlMillis = jwtTokenProvider.calculateTtlMillis(claims.getExpiration());
		String blacklistKey = getBlacklistPrefix() + accessToken;

		setOpsForValueRedis(blacklistKey, "blacklisted");
		redisTemplate.expire(blacklistKey, Math.max(ttlMillis, 1), TimeUnit.MILLISECONDS);
	}

	public String getUserTokenPrefix() {
		return USER_TOKEN_PREFIX;
	}

	private String getBlacklistPrefix() {
		return BLACKLIST_PREFIX;
	}

}