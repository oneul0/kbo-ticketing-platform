package com.boeingmerryho.business.userservice.application;

import java.time.LocalDate;

import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.response.inner.UserTokenResult;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;

public interface UserHelper {

	User findUserById(Long id);

	User findUserByEmail(String email);

	void validateRegisterRequest(UserAdminRegisterRequestServiceDto dto);

	void validateRegisterRequest(UserRegisterRequestServiceDto dto);

	void validateCommonFields(String email, String password, String username, String nickname, LocalDate birth);

	void validateRequiredStringField(String field, ErrorCode errorCode);

	void validateRequiredDateField(LocalDate field, ErrorCode errorCode);

	boolean isEmpty(String field);

	void verifyEmailFormat(String email);

	void verifyPasswordFormat(String password);

	void checkEmailExists(String email, UserRepository userRepository);

	String encodePassword(String password);

	void isAdminRole(UserRoleType type);

	UserTokenResult getUserTokenFromRedis(Long userId);

	void removeUserMembershipInfoFromRedis(Long id);

}

