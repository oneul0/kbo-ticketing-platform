package com.boeingmerryho.business.userservice.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.userservice.application.utils.RedisUtil;
import com.boeingmerryho.business.userservice.application.utils.jwt.JwtTokenProvider;
import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.exception.ErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUtilImpl implements RedisUtil {
	private static final String USER_INFO_PREFIX = "user:info:";
	private static final String USER_TOKEN_PREFIX = "user:token:";
	private static final String BLACKLIST_PREFIX = "blacklist:";
	private static final String MEMBERSHIP_INFO_PREFIX = "user:membership:info:";
	private final RedisTemplate<String, Object> redisTemplate;
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void updateUserInfo(User user) {
		String userInfoKey = USER_INFO_PREFIX + user.getId();
		Map<String, Object> userInfoMap = new ConcurrentHashMap<>();

		userInfoMap.put("email", user.getEmail());
		userInfoMap.put("username", user.getUsername());
		userInfoMap.put("nickname", user.getNickname());
		userInfoMap.put("role", user.getRole());
		userInfoMap.put("birth", user.getBirth());

		long expirationTime = jwtTokenProvider.getRefreshTokenExpiration();
		redisTemplate.delete(userInfoKey);
		redisTemplate.opsForHash().putAll(userInfoKey, userInfoMap);
		redisTemplate.expire(userInfoKey, expirationTime, TimeUnit.MILLISECONDS);
	}

	@Override
	public Map<String, String> updateUserJwtToken(Long id) {
		String accessToken = jwtTokenProvider.generateAccessToken(id);
		String refreshToken = jwtTokenProvider.generateRefreshToken(id);

		String tokenKey = USER_TOKEN_PREFIX + id;

		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("accessToken", accessToken);
		tokenMap.put("refreshToken", refreshToken);
		redisTemplate.delete(tokenKey);
		redisTemplate.opsForHash().putAll(tokenKey, tokenMap);

		long expirationTime = jwtTokenProvider.getRefreshTokenExpiration();
		redisTemplate.expire(tokenKey, expirationTime, TimeUnit.MILLISECONDS);

		return tokenMap;
	}

	@Override
	public void clearRedisUserData(Long userId) {
		redisTemplate.delete(USER_INFO_PREFIX + userId);
		redisTemplate.delete(USER_TOKEN_PREFIX + userId);
		redisTemplate.delete(MEMBERSHIP_INFO_PREFIX + userId);
	}

	@Override
	public void deleteFromRedisByKey(String tokenKey) {
		redisTemplate.delete(tokenKey);
	}

	@Override
	public Boolean hsaKeyInRedis(String key) {
		return Boolean.FALSE.equals(redisTemplate.hasKey(key));
	}

	@Override
	public Map<Object, Object> getMapEntriesFromRedis(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	@Override
	public String getOpsForValue(String key) {
		return (String)redisTemplate.opsForValue().get(key);
	}

	@Override
	public void setOpsForValueRedis(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public void setTtlAndOpsForValueRedis(String key, String value, Long timeout) {
		redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MINUTES);
	}

	@Override
	public void blacklistToken(String accessToken) {
		Claims claims = jwtTokenProvider.parseJwtToken(accessToken);
		long ttlMillis = jwtTokenProvider.calculateTtlMillis(claims.getExpiration());
		String blacklistKey = BLACKLIST_PREFIX + accessToken;

		setOpsForValueRedis(blacklistKey, "blacklisted");
		redisTemplate.expire(blacklistKey, Math.max(ttlMillis, 1), TimeUnit.MILLISECONDS);
	}

	@Override
	public void isEqualStoredRefreshToken(Long userId, String refreshToken) {
		String redisKey = USER_TOKEN_PREFIX + userId;
		String storedRefreshToken = (String)redisTemplate.opsForHash().get(redisKey, "refreshToken");

		if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
			throw new GlobalException(ErrorCode.JWT_NOT_MATCH);
		}
	}

}
