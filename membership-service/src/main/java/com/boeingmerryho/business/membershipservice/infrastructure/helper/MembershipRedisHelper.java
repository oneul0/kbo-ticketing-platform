package com.boeingmerryho.business.membershipservice.infrastructure.helper;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.exception.MembershipErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipRedisHelper {

	private final RedisTemplate<String, String> redisTemplate;

	private static final String MEMBERSHIP_STOCK_PREFIX = "membership:stock:";
	private static final String MEMBERSHIP_USER_PREFIX = "membership:user:";
	private static final String LUA_SCRIPT = """
			local stock = tonumber(redis.call("GET", KEYS[1]))
			if not stock or stock <= 0 then
				return -1
			end
			if redis.call("EXISTS", KEYS[2]) == 1 then
				return -2
			end
			redis.call("DECR", KEYS[1])
			redis.call("SET", KEYS[2], ARGV[2])
			redis.call("EXPIRE", KEYS[1], ARGV[3])
			redis.call("EXPIRE", KEYS[2], ARGV[3])
			return 1
		""";

	public void reserve(Long membershipId, Long userId, Duration ttl) {
		String stockKey = MEMBERSHIP_STOCK_PREFIX + membershipId;
		String userKey = MEMBERSHIP_USER_PREFIX + userId;

		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

		try {
			Long result = redisTemplate.execute(
				redisScript,
				List.of(stockKey, userKey),
				userId.toString(), membershipId.toString(), String.valueOf(ttl.getSeconds())
			);
			switch (result.intValue()) {
				case 1 -> {
				}
				case -1 -> throw new GlobalException(MembershipErrorCode.OUT_OF_STOCK);
				case -2 -> throw new GlobalException(MembershipErrorCode.ALREADY_RESERVED);
				default -> throw new GlobalException(MembershipErrorCode.UNKNOWN_ERROR);
			}
		} catch (Exception e) {
			throw new GlobalException(MembershipErrorCode.REDIS_ERROR);
		}
	}

	public void preloadStock(Long membershipId, Integer availableQuantity) {
		String stockKey = MEMBERSHIP_STOCK_PREFIX + membershipId;
		redisTemplate.opsForValue().set(stockKey, String.valueOf(availableQuantity));
	}

}