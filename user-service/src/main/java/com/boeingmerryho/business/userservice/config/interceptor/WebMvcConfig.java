package com.boeingmerryho.business.userservice.config.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.boeingmerryho.business.userservice.presentation.interceptor.AdminCheckInterceptor;
import com.boeingmerryho.business.userservice.presentation.interceptor.UserCheckInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final RedisTemplate<String, Object> redisTemplate;

	public WebMvcConfig(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AdminCheckInterceptor(redisTemplate))
			.excludePathPatterns(
				"/api/**",
				"/error/**",

				"/admin/v1/users/register",
				"/admin/v1/users/check",
				"/admin/v1/users/login"
			);

		registry.addInterceptor(new UserCheckInterceptor(redisTemplate))
			.excludePathPatterns(
				"/admin/**",
				"/error/**",
				
				"/api/v1/users/register",
				"/api/v1/users/check",
				"/api/v1/users/login"
			);
	}

}