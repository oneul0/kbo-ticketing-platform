package com.boeingmerryho.business.userservice.infrastructure.interceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestHeaderMdcInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String requestId = request.getHeader("X-Request-Id");
		String clientIp = request.getHeader("X-Client-Ip");
		String userAgent = request.getHeader("X-User-Agent");
		String method = request.getHeader("X-Method");
		String uri = request.getHeader("X-Uri");

		if (requestId != null)
			MDC.put("requestId", requestId);
		if (clientIp != null)
			MDC.put("ip", clientIp);
		if (userAgent != null)
			MDC.put("userAgent", userAgent);
		if (method != null)
			MDC.put("method", method);
		if (uri != null)
			MDC.put("uri", uri);

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
		Exception ex) {
		MDC.clear();
	}
}