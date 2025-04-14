package com.boeingmerryho.business.queueservice.presentation.interceptor;

import java.io.IOException;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AdminCheckInterceptor implements HandlerInterceptor {

	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		IOException {
		try {
			// log.info("admin preHandle");
			//
			// String userIdAttr = request.getHeader("X-User-Id");
			// log.info("login user : {}", userIdAttr);
			//
			// if (userIdAttr == null) {
			// 	log.info("userIdAttr id null");
			// 	return false;
			// }
			//
			// long userId = Long.parseLong(userIdAttr);
			// String redisKey = "user:info:" + userId;
			//
			// if (!redisTemplate.hasKey(redisKey)) {
			// 	log.info("user info key not found in redis");
			// 	return false;
			// }
			//
			// Map<Object, Object> userInfo = redisTemplate.opsForHash().entries(redisKey);
			// if (userInfo.isEmpty()) {
			// 	log.info("user info not found in redis");
			// 	return false;
			// }
			//
			// // if (!UserRoleType.ADMIN.name().equals(userInfo.get("role"))) {
			// // 	log.info("user is not admin");
			// // 	return false;
			// // }
			//
			// // 사용자 정보를 request 에 주입
			// request.setAttribute("userId", userId);
			// request.setAttribute("username", userInfo.get("username"));
			// request.setAttribute("slackId", userInfo.get("slackId"));
			// request.setAttribute("role", userInfo.get("role"));
			//
			// log.info("admin interceptor 완료");
			return true;

		} catch (Exception e) {
			log.info(e.getMessage());
			return false;
		}
	}

	private record ErrorResponse(String errorCode, String message) {
		static ErrorResponse of(String errorCode, String message) {
			return new ErrorResponse(errorCode, message);
		}
	}
}