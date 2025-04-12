package com.boeingmerryho.business.queueservice.infrastructure;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.queueservice.application.utils.RedisUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisUtilImpl implements RedisUtil {
	private final RedisTemplate<String, Object> redisTemplate;

}
