package com.boeingmerryho.business.membershipservice.infrastructure.helper;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.exception.MembershipErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MembershipRedisHelper {

	private final MeterRegistry meterRegistry;
	private final RedisTemplate<String, String> redisTemplate;
	private final Map<String, Counter> reserveFailCounters = new ConcurrentHashMap<>();

	private static final String MEMBERSHIP_TTL_PREFIX = "membership:ttl:";
	private static final String MEMBERSHIP_USER_PREFIX = "membership:users:";
	private static final String MEMBERSHIP_STOCK_PREFIX = "membership:stock:";
	private static final String MEMBERSHIP_ROLLBACK_ID_PREFIX = "membership:user:map:";
	private static final String LUA_SCRIPT = """
			if redis.call("SISMEMBER", KEYS[2], ARGV[1]) == 1 then
				return -2
			end
		
			local remain = tonumber(redis.call("DECR", KEYS[1]))
			if remain < 0 then
				redis.call("INCR", KEYS[1])
				return -1
			end
		
			redis.call("SADD", KEYS[2], ARGV[1])
			redis.call("SET", ARGV[2], 1, "EX", tonumber(ARGV[3]))
		 	redis.call("SET", ARGV[4], ARGV[5], "EX", tonumber(ARGV[3]))
			return 1
		""";
	// TODO redis command 각각 쪼개기 방법이랑 비교

	public void reserve(Long membershipId, Long userId, Duration ttl) {
		String ttlKey = MEMBERSHIP_TTL_PREFIX + userId;
		String mapKey = MEMBERSHIP_ROLLBACK_ID_PREFIX + userId;
		String userKey = MEMBERSHIP_USER_PREFIX + membershipId;
		String stockKey = MEMBERSHIP_STOCK_PREFIX + membershipId;

		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_SCRIPT, Long.class);

		Long result = redisTemplate.execute(
			redisScript,
			List.of(stockKey, userKey),
			userId.toString(),
			ttlKey, String.valueOf(ttl.getSeconds()),
			mapKey, membershipId.toString()
		);

		switch (result.intValue()) {
			case 1 -> {
			}
			case -1 -> {
				incrementReserveFailCounter("out_of_stock");
				throw new GlobalException(MembershipErrorCode.OUT_OF_STOCK);
			}
			case -2 -> {
				incrementReserveFailCounter("already_reserved");
				throw new GlobalException(MembershipErrorCode.ALREADY_RESERVED);
			}
			default -> {
				incrementReserveFailCounter("invalid_request");
				throw new GlobalException(MembershipErrorCode.UNKNOWN_ERROR);
			}
		}
	}

	private void incrementReserveFailCounter(String reason) {
		Counter counter = reserveFailCounters.computeIfAbsent(reason, r ->
			Counter.builder("membership_reserve_failed_count")
				.description("선점 실패 건수")
				.tag("reason", r)
				.register(meterRegistry)
		);
		counter.increment();
	}

	public void preloadStock(Long membershipId, Integer availableQuantity) {
		String stockKey = MEMBERSHIP_STOCK_PREFIX + membershipId;
		redisTemplate.opsForValue().set(stockKey, String.valueOf(availableQuantity));
	}

}