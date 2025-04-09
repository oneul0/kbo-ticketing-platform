package com.boeingmerryho.business.seatservice.infrastructure.helper;

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
public class SeatServiceHelper {
	private final SeatRepository seatRepository;

	public Seat getSeatById(Long id) {
		return seatRepository.findById(id)
			.orElseThrow(() -> new GlobalException(SeatErrorCode.NOT_FOUND_SEAT));
	}

	public void save(Seat seat) {
		seatRepository.save(seat);
	}
}