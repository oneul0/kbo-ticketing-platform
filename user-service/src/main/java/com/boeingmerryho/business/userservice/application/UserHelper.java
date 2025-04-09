package com.boeingmerryho.business.userservice.application;

import java.time.LocalDate;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.userservice.application.dto.request.UserAdminRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.UserRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;
import com.boeingmerryho.business.userservice.exception.UserException;

@Component
public class UserHelper {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$");

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

	public void updateRedisUserInfo(User user, RedisUtil redisUtil) {
		redisUtil.updateUserInfo(user);
	}
}