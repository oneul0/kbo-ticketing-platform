package com.boeingmerryho.business.queueservice.infrastructure;

import java.util.regex.Pattern;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.queueservice.application.QueueHelper;
import com.boeingmerryho.business.queueservice.application.utils.RedisUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QueueHelperImpl implements QueueHelper {


	private static final String MEMBERSHIP_INFO_PREFIX = "user:membership:info:";

	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisUtil redisUtil;

}