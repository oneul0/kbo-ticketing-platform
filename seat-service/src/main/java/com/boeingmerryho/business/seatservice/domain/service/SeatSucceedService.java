package com.boeingmerryho.business.seatservice.domain.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.seatservice.domain.ReservationStatus;
import com.boeingmerryho.business.seatservice.domain.Seat;
import com.boeingmerryho.business.seatservice.domain.SeatReservation;
import com.boeingmerryho.business.seatservice.exception.SeatErrorCode;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatCommonHelper;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatListenerHelper;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatSucceedService {
	private final RedissonClient redissonClient;
	private final SeatCommonHelper seatCommonHelper;
	private final SeatListenerHelper seatListenerHelper;

	@Transactional
	public void succeed(List<String> seatIds, LocalDate date) {
		for (String seatId : seatIds) {
			Seat seat = seatListenerHelper.getSeat(seatId);
			RList<String> blockSeats = seatListenerHelper.getCacheBlocks(seat, date);

			String cacheSeatKey = seatCommonHelper.makeCacheKey(seat, date);

			if (blockSeats.contains(cacheSeatKey)) {
				RBucket<Map<String, String>> seatBucketKey = redissonClient.getBucket(cacheSeatKey);
				Map<String, String> seatBucketValue = seatBucketKey.get();

				seatListenerHelper.checkStatusProcessing(seatBucketValue.get("status"));

				seatBucketValue.put("status", ReservationStatus.COMPLETED.name());

				seatBucketKey.set(seatBucketValue, Duration.ofMinutes(5));
				log.info("좌석: {}, 선점 완료", seatBucketKey.getName());

				Long userId = seatListenerHelper.parseUserId(seatBucketValue.get("userId"));

				SeatReservation seatReservation = SeatReservation.builder()
					.seat(seat)
					.userId(userId)
					.reservationDate(date)
					.isReserved(true)
					.createdAt(LocalDateTime.now())
					.createdBy(userId)
					.build();

				seat.getSeatReservations().add(seatReservation);
				seatCommonHelper.save(seat);
			} else {
				throw new GlobalException(SeatErrorCode.NOT_FOUND_BLOCK);
			}
		}
	}
}