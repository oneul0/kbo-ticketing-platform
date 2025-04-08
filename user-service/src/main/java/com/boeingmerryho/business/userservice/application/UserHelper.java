package com.boeingmerryho.business.userservice.application;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;
import com.boeingmerryho.business.userservice.exception.UserException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserHelper {

	private static final String EMAIL_REGEX = "^[a-z0-9]{4,10}$";
	private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,15}$";

	public void emailVerify(String email) {
		if (!Pattern.matches(EMAIL_REGEX, email)) {
			//todo: email null error
		}
	}

	public void passwordVerify(String password) {
		if (!Pattern.matches(PASSWORD_REGEX, password)) {
			throw new UserException(ErrorCode.PASSWORD_REGEX_NOT_MATCH);
		}
	}

	public void validateRequiredField(String value, ErrorCode errorCode) {
		if (StringUtils.isBlank(value)) {
			throw new UserException(errorCode);
		}
	}

	public void checkUsernameExists(String username, UserRepository userRepository) {
		if (userRepository.existsByEmail(username)) {
			throw new UserException(ErrorCode.ALREADY_EXISTS);
		}
	}

	public User findUserById(Long id, UserRepository userRepository) {
		return userRepository.findById(id)
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));
	}

	public User findUserByEmail(String username, UserRepository userRepository) {
		return userRepository.findByEmail(username)
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND));
	}

	public String encodePassword(String password, PasswordEncoder passwordEncoder) {
		return passwordEncoder.encode(password);
	}

	public void updateRedisUserInfo(User user, RedisUtil redisUtil) {
		redisUtil.updateUserInfo(user);
	}

	public void checkMasterRole(User user) {
		if (!user.getRole().equals(UserRoleType.ADMIN)) {
			throw new UserException(ErrorCode.LESS_ROLE);
		}
	}
}