package com.boeingmerryho.business.queueservice.presentation.interceptor;

import java.io.IOException;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserCheckInterceptor implements HandlerInterceptor {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
		IOException {
		try {
			log.info("user preHandle");

			String userIdAttr = request.getHeader("X-User-Id");
			log.info("login user : {}", userIdAttr);
			if (userIdAttr == null) {
				return false;
			}

			long userId = Long.parseLong(userIdAttr);
			String redisKey = "user:info:" + userId;

			if (!redisTemplate.hasKey(redisKey)) {
				return false;
			}

			Map<Object, Object> userInfo = redisTemplate.opsForHash().entries(redisKey);
			if (userInfo.isEmpty()) {
				return false;
			}

			request.setAttribute("userId", userId);
			request.setAttribute("username", userInfo.get("username"));
			request.setAttribute("slackId", userInfo.get("slackId"));
			request.setAttribute("role", userInfo.get("role"));
			log.info("login user info setting complete");
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