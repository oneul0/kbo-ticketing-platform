package com.boeingmerryho.business.seatservice.infrastructure.helper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.seatservice.domain.Seat;
import com.boeingmerryho.business.seatservice.exception.SeatErrorCode;
import com.boeingmerryho.business.seatservice.infrastructure.SeatRepository;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatCommonHelper {
	private final SeatRepository seatRepository;

	public final String seatPrefix = "seat:";
	public final String seatKeyPrefix = "seatsKey:";

	public Seat getSeatById(Long id) {
		return seatRepository.findById(id)
			.orElseThrow(() -> new GlobalException(SeatErrorCode.NOT_FOUND_SEAT));
	}

	public void save(Seat seat) {
		seatRepository.save(seat);
	}

	public String makeCacheKey(Seat seat, LocalDate date) {
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

	public String createCacheBlockKey(Integer blockId, LocalDate date) {
		StringBuilder cacheBlockKey = new StringBuilder()
			.append(seatKeyPrefix)
			.append(date)
			.append(":")
			.append(blockId);

		return cacheBlockKey.toString();
	}
}