package com.boeingmerryho.business.userservice.infrastructure.helper;

import java.time.LocalDate;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.userservice.application.UserHelper;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.response.inner.UserTokenResult;
import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserHelperImpl implements UserHelper {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$");

	private static final String USER_TOKEN_PREFIX = "user:token:";
	private static final String MEMBERSHIP_INFO_PREFIX = "user:membership:info:";

	private final RedisUtil redisUtil;
	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Override
	public User findUserById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
	}

	@Override
	public void validateRegisterRequest(UserAdminRegisterRequestServiceDto dto) {
		validateCommonFields(dto.email(), dto.password(), dto.username(), dto.nickname(), dto.birth());
		checkEmailExists(dto.email(), userRepository);
	}

	@Override
	public void validateRegisterRequest(UserRegisterRequestServiceDto dto) {
		validateCommonFields(dto.email(), dto.password(), dto.username(), dto.nickname(), dto.birth());
		checkEmailExists(dto.email(), userRepository);
	}

	@Override
	public void validateCommonFields(String email, String password, String username, String nickname,
		LocalDate birth) {
		validateRequiredStringField(email, ErrorCode.EMAIL_NULL);
		validateRequiredStringField(password, ErrorCode.PASSWORD_NULL);
		validateRequiredStringField(username, ErrorCode.USERNAME_NULL);
		validateRequiredStringField(nickname, ErrorCode.USERNAME_NULL);
		validateRequiredDateField(birth, ErrorCode.USERNAME_NULL);

		verifyEmailFormat(email);
		verifyPasswordFormat(password);
	}

	@Override
	public void validateRequiredStringField(String field, ErrorCode errorCode) {
		if (isEmpty(field)) {
			throw new GlobalException(errorCode);
		}
	}

	@Override
	public void validateRequiredDateField(LocalDate field, ErrorCode errorCode) {
		if (field == null) {
			throw new GlobalException(errorCode);
		}
	}

	@Override
	public boolean isEmpty(String field) {
		return field == null || field.trim().isEmpty();
	}

	@Override
	public void verifyEmailFormat(String email) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			throw new GlobalException(ErrorCode.USERNAME_REGEX_NOT_MATCH);
		}
	}

	@Override
	public void verifyPasswordFormat(String password) {
		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			throw new GlobalException(ErrorCode.PASSWORD_REGEX_NOT_MATCH);
		}
	}

	@Override
	public void checkEmailExists(String email, UserRepository userRepository) {
		if (userRepository.existsByEmail(email)) {
			throw new GlobalException(ErrorCode.ALREADY_EXISTS);
		}
	}

	@Override
	public String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	@Override
	public void isAdminRole(UserRoleType type) {
		if (type.equals(UserRoleType.ADMIN)) {
			throw new GlobalException(ErrorCode.CANNOT_GRANT_MASTER_ROLE);
		}
	}

	@Override
	public UserTokenResult getUserTokenFromRedis(Long userId) {
		String tokenKey = USER_TOKEN_PREFIX + userId;

		if (redisUtil.hsaKeyInRedis(tokenKey)) {
			throw new GlobalException(ErrorCode.NOT_FOUND);
		}

		Map<Object, Object> token = redisUtil.getMapEntriesFromRedis(tokenKey);
		if (token == null || token.isEmpty()) {
			throw new GlobalException(ErrorCode.JWT_REQUIRED);
		}
		return new UserTokenResult(tokenKey, token);
	}

	@Override
	public void removeUserMembershipInfoFromRedis(Long id) {
		String membershipKey = MEMBERSHIP_INFO_PREFIX + id;
		redisUtil.deleteFromRedisByKey(membershipKey);
	}

}