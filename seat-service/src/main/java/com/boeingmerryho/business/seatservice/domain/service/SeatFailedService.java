package com.boeingmerryho.business.seatservice.domain.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.seatservice.domain.ReservationStatus;
import com.boeingmerryho.business.seatservice.domain.Seat;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatListenerHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatFailedService {
	private final RedissonClient redissonClient;
	private final SeatListenerHelper seatListenerHelper;

	@Transactional
	public void failed(List<String> seatIds, LocalDate date) {
		for (String seatId : seatIds) {
			Seat seat = seatListenerHelper.getSeat(seatId);
			RList<String> blockSeats = seatListenerHelper.getCacheBlocks(seat, date);

			String cacheSeatKey = seatListenerHelper.makeCacheKey(seat, date);

			if (blockSeats.contains(cacheSeatKey)) {
				RBucket<Map<String, String>> seatBucketKey = redissonClient.getBucket(cacheSeatKey);
				Map<String, String> seatBucketValue = seatBucketKey.get();

				seatListenerHelper.checkStatusProcessing(seatBucketValue.get("status"));

				seatBucketValue.put("status", ReservationStatus.AVAILABLE.name());
				seatBucketValue.put("userId", null);
				seatBucketValue.put("createdAt", null);
				seatBucketValue.put("expiredAt", null);

				seatBucketKey.set(seatBucketValue, Duration.ofMinutes(5));
				log.info("좌석: {}, 선점 실패 처리", seatBucketKey.getName());
			}
		}
	}
}