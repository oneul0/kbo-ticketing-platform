package com.boeingmerryho.business.seatservice.domain.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.seatservice.domain.ReservationStatus;
import com.boeingmerryho.business.seatservice.domain.Seat;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateCacheSeatsService {
	private final RedissonClient redissonClient;

	@Transactional
	public void createSeatBucket(List<Seat> seats, LocalDate date) {
		for (Seat seat : seats) {
			String cacheKey = makeCacheKey(seat, date);
			Map<String, String> cacheValue = makeCacheValue();

			RBucket<Map<String, String>> bucket = redissonClient.getBucket(cacheKey);
			// TODO: 테스트 기간까지만 5분으로 설정
			bucket.set(cacheValue, Duration.ofMinutes(5));
		}
	}

	private String makeCacheKey(Seat seat, LocalDate date) {
		StringBuilder builder = new StringBuilder()
			.append("seat:")
			.append(date)
			.append(":")
			.append(seat.getSeatBlock())
			.append(":")
			.append(seat.getSeatColumn())
			.append(":")
			.append(seat.getSeatRow());

		return builder.toString();
	}

	private Map<String, String> makeCacheValue() {
		Map<String, String> values = new HashMap<>();

		values.put("status", ReservationStatus.AVAILABLE.name());
		values.put("userId", null);
		values.put("createdAt", null);
		values.put("expiredAt", null);

		return values;
	}
}