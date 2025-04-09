package com.boeingmerryho.business.seatservice.infrastructure.helper;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.seatservice.domain.Seat;
import com.boeingmerryho.business.seatservice.infrastructure.SeatRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatAdminServiceHelper {
	private final SeatRepository seatRepository;

	public void delete(Seat seat) {
		seatRepository.delete(seat);
	}
}