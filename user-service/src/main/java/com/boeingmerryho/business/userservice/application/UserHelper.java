package com.boeingmerryho.business.userservice.application;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;

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

	String encodePassword(String password, PasswordEncoder passwordEncoder);

	void isAdminRole(UserRoleType type);

	void isEqualStoredRefreshToken(Long userId, String refreshToken);

	void updateRedisUserInfo(User user);

	Map<String, String> updateUserJwtTokenRedis(Long id);

	void clearRedisUserData(Long userId);

	void deleteFromRedisByKey(String tokenKey);

	void hasKeyInRedis(String key);

	Map<Object, Object> getMapEntriesFromRedis(String key);

	void setOpsForValueRedis(String key, String value);

	void blacklistToken(String accessToken);

	UserTokenResult getUserTokenFromRedis(Long userId);

	String generateVerificationCode();

	void storeVerificationCode(String email, String code);

	String getVerificationCode(String email);

	void removeVerificationCode(String email);

	void checkDuplicatedVerificationRequest(String email);

	String getNotifyLoginResponse(Long id);

	void removeUserMembershipInfoFromRedis(Long id);
}

