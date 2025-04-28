package com.boeingmerryho.business.paymentservice.infrastructure.config.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.boeingmerryho.commonlibrary.interceptor.AdminCheckInterceptor;
import io.github.boeingmerryho.commonlibrary.interceptor.UserCheckInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final RedisUserInfoProvider userInfoProvider;

	public WebMvcConfig(RedisUserInfoProvider userInfoProvider) {
		this.userInfoProvider = userInfoProvider;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new UserCheckInterceptor(userInfoProvider))
			.excludePathPatterns(
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
