package com.boeingmerryho.business.seatservice.infrastructure.helper;

import java.time.LocalDate;

import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.seatservice.domain.ReservationStatus;
import com.boeingmerryho.business.seatservice.domain.Seat;
import com.boeingmerryho.business.seatservice.exception.SeatErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatListenerHelper {
	private final RedissonClient redissonClient;
	private final SeatCommonHelper seatCommonHelper;

	public void checkStatusProcessing(String status) {
		if (!status.equals(ReservationStatus.PROCESSING.toString())) {
			throw new GlobalException(SeatErrorCode.INVALID_ACCESS);
		}
	}

	public LocalDate parseLocalDate(String date) {
		return LocalDate.parse(date);
	}

	public Long parseUserId(String userId) {
		return Long.parseLong(userId);
	}

	public Seat getSeat(String seatId) {
		Long id = Long.parseLong(seatId);
		return seatCommonHelper.getSeatById(id);
	}

	public RList<String> getCacheBlocks(Seat seat, LocalDate date) {
		String cacheBlockKey = createCacheBlockKey(seat.getSeatBlock(), date);

		return redissonClient.getList(cacheBlockKey);
	}

	public String createCacheBlockKey(Integer blockId, LocalDate date) {
		String seatPrefix = "seat:";

		StringBuilder cacheBlockKey = new StringBuilder()
			.append(seatPrefix)
			.append(date)
			.append(":")
			.append(blockId);

		return cacheBlockKey.toString();
	}

	public String makeCacheKey(Seat seat, LocalDate date) {
		String seatPrefix = "seat:";

		StringBuilder builder = new StringBuilder()
			.append(seatPrefix)
			.append(date)
			.append(":")
			.append(seat.getSeatBlock())
			.append(":")
			.append(seat.getSeatColumn())
			.append(":")
			.append(seat.getSeatRow());

		return builder.toString();
	}
}