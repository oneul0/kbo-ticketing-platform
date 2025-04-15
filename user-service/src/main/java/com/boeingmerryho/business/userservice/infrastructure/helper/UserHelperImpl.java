package com.boeingmerryho.business.userservice.infrastructure.helper;

import java.time.LocalDate;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.userservice.application.UserHelper;
import com.boeingmerryho.business.userservice.application.dto.request.admin.UserAdminRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.request.feign.LoginSuccessRequest;
import com.boeingmerryho.business.userservice.application.dto.request.other.UserRegisterRequestServiceDto;
import com.boeingmerryho.business.userservice.application.dto.response.inner.UserTokenResult;
import com.boeingmerryho.business.userservice.application.feign.MembershipClient;
import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;

import feign.FeignException;
import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserHelperImpl implements UserHelper {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$");

	private static final String USER_TOKEN_PREFIX = "user:token:";
	private static final String VERIFICATION_PREFIX = "verification:email:";
	private static final String MEMBERSHIP_INFO_PREFIX = "user:membership:info:";
	private static final String USER_INFO_PREFIX = "user:info:";
	private static final String BLACKLIST_PREFIX = "blacklist:";

	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisUtil redisUtil;
	private final UserRepository userRepository;
	private final MembershipClient membershipClient;

	public User findUserById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
	}

	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
	}

	//todo: 합칠 수 있으면 합치기
	public void validateRegisterRequest(UserAdminRegisterRequestServiceDto dto) {
		validateCommonFields(dto.email(), dto.password(), dto.username(), dto.nickname(), dto.birth());
		checkEmailExists(dto.email(), userRepository);
	}

	public void validateRegisterRequest(UserRegisterRequestServiceDto dto) {
		validateCommonFields(dto.email(), dto.password(), dto.username(), dto.nickname(), dto.birth());
		checkEmailExists(dto.email(), userRepository);
	}

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

	public void validateRequiredStringField(String field, ErrorCode errorCode) {
		if (isEmpty(field)) {
			throw new GlobalException(errorCode);
		}
	}

	public void validateRequiredDateField(LocalDate field, ErrorCode errorCode) {
		if (field == null) {
			throw new GlobalException(errorCode);
		}
	}

	public boolean isEmpty(String field) {
		return field == null || field.trim().isEmpty();
	}

	public void verifyEmailFormat(String email) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			throw new GlobalException(ErrorCode.USERNAME_REGEX_NOT_MATCH);
		}
	}

	public void verifyPasswordFormat(String password) {
		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			throw new GlobalException(ErrorCode.PASSWORD_REGEX_NOT_MATCH);
		}
	}

	public void checkEmailExists(String email, UserRepository userRepository) {
		if (userRepository.existsByEmail(email)) {
			throw new GlobalException(ErrorCode.ALREADY_EXISTS);
		}
	}

	public String encodePassword(String password, PasswordEncoder passwordEncoder) {
		return passwordEncoder.encode(password);
	}

	public void isAdminRole(UserRoleType type) {
		if (type.equals(UserRoleType.ADMIN)) {
			throw new GlobalException(ErrorCode.CANNOT_GRANT_MASTER_ROLE);
		}
	}

	public void updateRedisUserInfo(User user) {
		redisUtil.updateUserInfo(user);
	}

	public Map<String, String> updateUserJwtTokenRedis(Long id) {
		return redisUtil.updateUserJwtToken(id);
	}

	public UserTokenResult getUserTokenFromRedis(Long userId) {
		String tokenKey = USER_TOKEN_PREFIX + userId;
		redisUtil.hasKeyInRedis(tokenKey);

		Map<Object, Object> token = getMapEntriesFromRedis(tokenKey);
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

	//----mail
	public String generateVerificationCode() {
		return String.format("%06d", new Random().nextInt(1000000));
	}

	@Override
	public void storeVerificationCode(String email, String code) {
		String key = VERIFICATION_PREFIX + email;
		redisUtil.setTtlAndOpsForValueRedis(key, code, 5L);
	}

	@Override
	public String getVerificationCode(String email) {
		String key = VERIFICATION_PREFIX + email;
		return redisUtil.getOpsForValue(key);
	}

	@Override
	public void removeVerificationCode(String email) {
		String key = VERIFICATION_PREFIX + email;
		redisUtil.deleteFromRedisByKey(key);
	}

	@Override
	public void checkDuplicatedVerificationRequest(String email) {
		String key = VERIFICATION_PREFIX + email;
		if (redisTemplate.hasKey(key)) {
			throw new GlobalException(ErrorCode.VERIFICATION_ALREADY_SENT);
		}
	}

	public String getNotifyLoginResponse(Long id) {
		LoginSuccessRequest request = new LoginSuccessRequest(id);
		try {
			ResponseEntity<String> response = membershipClient.notifyLogin(request);
			return response.getBody();
		} catch (FeignException e) {
			if (e.status() >= 400 && e.status() < 500) {
				throw new GlobalException(ErrorCode.MEMBERSHIP_INFO_SETTING_FAIL);
			} else {
				throw new GlobalException(ErrorCode.MEMBERSHIP_FEIGN_REQUEST_FAIL);
			}
		} catch (Exception e) {
			throw new GlobalException(ErrorCode.MEMBERSHIP_FEIGN_REQUEST_FAIL);
		}
	}

}