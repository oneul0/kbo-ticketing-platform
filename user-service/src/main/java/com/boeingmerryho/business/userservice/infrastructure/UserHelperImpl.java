package com.boeingmerryho.business.userservice.infrastructure;

import java.time.LocalDate;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
import com.boeingmerryho.business.userservice.application.utils.jwt.JwtTokenProvider;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.domain.repository.UserRepository;
import com.boeingmerryho.business.userservice.exception.ErrorCode;

import feign.FeignException;
import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserHelperImpl implements UserHelper {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,15}$");

	private static final String USER_INFO_PREFIX = "user:info:";
	private static final String USER_TOKEN_PREFIX = "user:token:";
	private static final String BLACKLIST_PREFIX = "blacklist:";
	private static final String VERIFICATION_PREFIX = "verification:email:";
	private static final String MEMBERSHIP_INFO_PREFIX = "user:membership:info:";

	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisUtil redisUtil;
	private final UserRepository userRepository;
	private final MembershipClient membershipClient;

	public User findUserById(Long id, UserRepository userRepository) {
		return userRepository.findById(id)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
	}

	public User findUserByEmail(String email, UserRepository userRepository) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
	}

	public void validateRegisterRequest(UserAdminRegisterRequestServiceDto dto, UserRepository userRepository) {
		validateCommonFields(dto.email(), dto.password(), dto.username(), dto.nickname(), dto.birth());
		checkEmailExists(dto.email(), userRepository);
	}

	public void validateRegisterRequest(UserRegisterRequestServiceDto dto, UserRepository userRepository) {
		validateCommonFields(dto.email(), dto.password(), dto.username(), dto.nickname(), dto.birth());
		checkEmailExists(dto.email(), userRepository);
	}

	public void validateCommonFields(String email, String password, String username, String nickname,
		LocalDate birth) {
		validateRequiredField(email, ErrorCode.EMAIL_NULL);
		validateRequiredField(password, ErrorCode.PASSWORD_NULL);
		validateRequiredField(username, ErrorCode.USERNAME_NULL);
		validateRequiredField(nickname, ErrorCode.USERNAME_NULL);
		validateRequiredField(birth, ErrorCode.USERNAME_NULL);

		verifyEmailFormat(email);
		verifyPasswordFormat(password);
	}

	public void validateRequiredField(String field, ErrorCode errorCode) {
		if (isEmpty(field)) {
			throw new GlobalException(errorCode);
		}
	}

	public void validateRequiredField(LocalDate field, ErrorCode errorCode) {
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

	public void checkMasterRole(User user) {
		if (user.isAdmin()) {
			throw new GlobalException(ErrorCode.CANNOT_GRANT_MASTER_ROLE);
		}
	}

	public void isAdminRole(UserRoleType type) {
		if (type.equals(UserRoleType.ADMIN)) {
			throw new GlobalException(ErrorCode.CANNOT_GRANT_MASTER_ROLE);
		}
	}

	//-----jwt
	public void isValidRefreshToken(String refreshToken) {
		jwtTokenProvider.validateRefreshToken(refreshToken);
	}

	public Long getUserIdFromToken(String refreshToken) {
		return Long.valueOf(jwtTokenProvider.getUserIdFromToken(refreshToken));
	}

	public void isEqualStoredRefreshToken(Long userId, String refreshToken) {
		String redisKey = USER_TOKEN_PREFIX + userId;
		String storedRefreshToken = (String)redisTemplate.opsForHash().get(redisKey, "refreshToken");

		if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
			throw new GlobalException(ErrorCode.JWT_NOT_MATCH);
		}

		findUserById(userId, userRepository);
	}

	public String generateAccessToken(Long userId) {
		return jwtTokenProvider.generateAccessToken(userId);
	}

	//-----redis

	public void updateRedisUserInfo(User user) {
		redisUtil.updateUserInfo(user);
	}

	public Map<String, String> updateUserJwtTokenRedis(Long id) {
		return redisUtil.updateUserJwtToken(id);
	}

	public void clearRedisUserData(Long userId) {
		redisTemplate.delete(USER_INFO_PREFIX + userId);
		redisTemplate.delete(USER_TOKEN_PREFIX + userId);
		redisTemplate.delete(MEMBERSHIP_INFO_PREFIX + userId);
	}

	public void deleteKeyFromRedis(String tokenKey) {
		redisTemplate.delete(tokenKey);
	}

	public void hasKeyInRedis(String key) {
		if (!redisTemplate.hasKey(key)) {
			throw new GlobalException(ErrorCode.NOT_FOUND);
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
		String blacklistKey = BLACKLIST_PREFIX + accessToken;

		setOpsForValueRedis(blacklistKey, "blacklisted");
		redisTemplate.expire(blacklistKey, Math.max(ttlMillis, 1), TimeUnit.MILLISECONDS);

	}

	public UserTokenResult getUserTokenFromRedis(Long userId) {
		String tokenKey = USER_TOKEN_PREFIX + userId;
		hasKeyInRedis(tokenKey);

		Map<Object, Object> token = getMapEntriesFromRedis(tokenKey);
		if (token == null || token.isEmpty()) {
			throw new GlobalException(ErrorCode.JWT_REQUIRED);
		}
		return new UserTokenResult(tokenKey, token);
	}

	//----mail
	public String generateVerificationCode() {
		return String.format("%06d", new Random().nextInt(1000000)); // 6자리 랜덤 숫자
	}

	public void storeVerificationCode(String email, String code) {
		String key = VERIFICATION_PREFIX + email;
		redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES); // 5분 TTL
	}

	public String getVerificationCode(String email) {
		String key = VERIFICATION_PREFIX + email;
		return (String)redisTemplate.opsForValue().get(key);
	}

	public void removeVerificationCode(String email) {
		String key = VERIFICATION_PREFIX + email;
		redisTemplate.delete(key);
	}

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

	public void removeUserMembershipInfoFromRedis(Long id) {
		String membershipKey = MEMBERSHIP_INFO_PREFIX + id;
		redisTemplate.delete(membershipKey);
	}

}