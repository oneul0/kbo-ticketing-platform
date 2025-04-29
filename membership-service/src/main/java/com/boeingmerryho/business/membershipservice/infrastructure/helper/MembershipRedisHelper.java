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

	private static final String MEMBERSHIP_STOCK_PREFIX = "membership:stock:";
	private static final String MEMBERSHIP_USER_PREFIX = "membership:user:";
	private static final String LUA_SCRIPT = """
			local remain = tonumber(redis.call("DECR", KEYS[1]))
			if remain < 0 then
				redis.call("INCR", KEYS[1]);
				return -1
			end
			if redis.call("EXISTS", KEYS[2]) == 1 then
				return -2
			end
			redis.call("SET", KEYS[2], ARGV[2], "EX", tonumber(ARGV[3]))
			return 1
		""";
	// TODO redis command 각각 쪼개기 방법이랑 비교
	// TODO SET 자료구조 활용하면 심플하게 변경 가능하니 고려

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
		} catch (Exception e) {
			incrementReserveFailCounter("redis_exception");
			throw new GlobalException(MembershipErrorCode.REDIS_ERROR);
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