package com.boeingmerryho.business.seatservice.domain.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.redisson.api.RBatch;
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
		final int batchSize = 1000;
		int operationCount = 0;

		RBatch batch = redissonClient.createBatch();
		Map<String, List<String>> blockSeatKeysMap = new HashMap<>();

		for (Seat seat : seats) {
			String cacheKey = seatCommonHelper.makeCacheKey(seat, date);
			Map<String, String> cacheValue = makeCacheValue(seat);

			batch.getBucket(cacheKey).setAsync(cacheValue, Duration.ofMinutes(20));

			String blockKey = makeBlockKey(seat, date);
			blockSeatKeysMap.computeIfAbsent(blockKey, k -> new ArrayList<>()).add(cacheKey);

			operationCount++;

			if (operationCount % batchSize == 0) {
				executeBatch(batch, blockSeatKeysMap);

				batch = redissonClient.createBatch();
				blockSeatKeysMap.clear();
			}
		}

		if (!blockSeatKeysMap.isEmpty()) {
			executeBatch(batch, blockSeatKeysMap);
		}
	}

	private void executeBatch(RBatch batch, Map<String, List<String>> blockSeatKeysMap) {
		for (Map.Entry<String, List<String>> entry : blockSeatKeysMap.entrySet()) {
			batch.getSet(entry.getKey()).addAllAsync(entry.getValue());
		}

		batch.execute();
	}

	private Map<String, String> makeCacheValue(Seat seat) {
		Map<String, String> values = new HashMap<>();

		values.put("id", seat.getId().toString());
		values.put("userId", null);
		values.put("status", ReservationStatus.AVAILABLE.name());
		values.put("price", seat.getPrice().toString());
		values.put("isSenior", seat.getIsSenior().toString());
		values.put("createdAt", null);
		values.put("expiredAt", null);

		return values;
	}

	private String makeBlockKey(Seat seat, LocalDate date) {
		StringBuilder builder = new StringBuilder()
			.append(seatCommonHelper.seatKeyPrefix)
			.append(date)
			.append(":")
			.append(seat.getSeatBlock());

		return builder.toString();
	}
}