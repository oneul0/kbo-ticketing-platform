package com.boeingmerryho.business.seatservice.config.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.boeingmerryho.commonlibrary.interceptor.AdminCheckInterceptor;
import io.github.boeingmerryho.commonlibrary.interceptor.UserCheckInterceptor;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
	private final RedisUserInfoProvider userInfoProvider;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new UserCheckInterceptor(userInfoProvider))
			.excludePathPatterns(
				"/admin/**",
				"/error/**",
				"/actuator/**"
			);

		registry.addInterceptor(new AdminCheckInterceptor(userInfoProvider))
			.excludePathPatterns(
				"/api/**",
				"/error/**",
				"/actuator/**"
			);
	}
}