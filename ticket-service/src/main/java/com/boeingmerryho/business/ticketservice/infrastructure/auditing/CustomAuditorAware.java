package com.boeingmerryho.business.ticketservice.infrastructure.auditing;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CustomAuditorAware implements AuditorAware<Long> {

	private static final ThreadLocal<Long> auditorHolder = new ThreadLocal<>();

	public static void setAuditor(Long userId) {
		auditorHolder.set(userId);
	}

	public static void clear() {
		auditorHolder.remove();
	}

	@Override
	public Optional<Long> getCurrentAuditor() {
		// 1. ThreadLocal 우선
		if (auditorHolder.get() != null) {
			return Optional.of(auditorHolder.get());
		}

		// 2. HTTP 요청에서 가져오기
		ServletRequestAttributes attributes =
			(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		if (attributes != null) {
			String userId = attributes.getRequest().getHeader("X-User-Id");
			if (userId != null) {
				try {
					return Optional.of(Long.parseLong(userId));
				} catch (NumberFormatException ignored) {}
			}
		}

		return Optional.empty();
	}
}