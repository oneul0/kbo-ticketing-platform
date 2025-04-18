package com.boeingmerryho.business.userservice.config.auth;

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
		registry.addInterceptor(new AdminCheckInterceptor(userInfoProvider))
			.excludePathPatterns(
				"/api/**",
				"/error/**",

				"/admin/v1/users/register",
				"/admin/v1/users/check",
				"/admin/v1/users/login"
			);

		registry.addInterceptor(new UserCheckInterceptor(userInfoProvider))
			.excludePathPatterns(
				"/admin/**",
				"/error/**",

				"/api/v1/users/register",
				"/api/v1/users/check",
				"/api/v1/users/login"
			);
	}

}