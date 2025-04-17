package com.boeingmerryho.business.seatservice.domain.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.seatservice.domain.ReservationStatus;
import com.boeingmerryho.business.seatservice.domain.Seat;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatCommonHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateCacheSeatsService {
	private final RedissonClient redissonClient;
	private final SeatCommonHelper seatCommonHelper;

	@Transactional
	public void createSeatBucket(List<Seat> seats, LocalDate date) {
		for (Seat seat : seats) {
			String cacheKey = seatCommonHelper.makeCacheKey(seat, date);
			Map<String, String> cacheValue = makeCacheValue(seat);

			RBucket<Map<String, String>> bucket = redissonClient.getBucket(cacheKey);
			// TODO: 테스트 기간까지만 5분으로 설정
			bucket.set(cacheValue, Duration.ofMinutes(600));

			String blockKey = makeBlockKey(seat, date);
			RList<String> blockSeats = redissonClient.getList(blockKey);

			if (!blockSeats.contains(cacheKey)) {
				blockSeats.add(cacheKey);
			}
		}
	}

	private Map<String, String> makeCacheValue(Seat seat) {
		Map<String, String> values = new HashMap<>();

		values.put("id", seat.getId().toString());
		values.put("userId", null);
		values.put("status", ReservationStatus.AVAILABLE.name());
		values.put("price", seat.getPrice().toString());
		values.put("createdAt", null);
		values.put("expiredAt", null);

		return values;
	}

	private String makeBlockKey(Seat seat, LocalDate date) {
		StringBuilder builder = new StringBuilder()
			.append(seatCommonHelper.seatPrefix)
			.append(date)
			.append(":")
			.append(seat.getSeatBlock());

		return builder.toString();
	}
}