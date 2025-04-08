package com.boeingmerryho.business.userservice.application.utils;

import java.util.Map;

import com.boeingmerryho.business.userservice.domain.User;

public interface RedisUtil {
	void updateUserInfo(User user);

	Map<String, String> updateUserJwtToken(Long id);

}
