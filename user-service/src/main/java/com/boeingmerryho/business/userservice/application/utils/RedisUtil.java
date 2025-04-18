package com.boeingmerryho.business.userservice.application.utils;

import java.util.Map;

import com.boeingmerryho.business.userservice.domain.User;

public interface RedisUtil {
	void updateUserInfo(User user);

	Map<String, String> updateUserJwtToken(Long id);

	void clearRedisUserData(Long userId);

	void deleteFromRedisByKey(String tokenKey);

	Boolean hasKeyInRedis(String key);

	Map<Object, Object> getMapEntriesFromRedis(String key);

	String getOpsForValue(String key);

	void setOpsForValueRedis(String key, String value);

	void setTtlAndOpsForValueRedis(String key, String value, Long timeout);

	void blacklistToken(String accessToken);

	void isEqualStoredRefreshToken(Long userId, String refreshToken);

	void rollbackUserInfo(Long id);

	void rollbackUserJwtToken(Long id);
}
