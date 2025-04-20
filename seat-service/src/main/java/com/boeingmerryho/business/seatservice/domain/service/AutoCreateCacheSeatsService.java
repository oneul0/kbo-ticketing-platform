package com.boeingmerryho.business.seatservice.domain.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.domain.Seat;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatAdminServiceHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoCreateCacheSeatsService {
	private final SeatAdminServiceHelper seatAdminServiceHelper;
	private final CreateCacheSeatsService createCacheSeatsService;

	public void preloadCacheSeats() {
		LocalDate todayPlusSevenDay = LocalDate.now().plusDays(7);

		List<Seat> seats = seatAdminServiceHelper.getSeatsByIsActiveIsTrue();
		createCacheSeatsService.createSeatBucket(seats, todayPlusSevenDay);

		log.info("날짜: {} - 좌석 생성 완료", todayPlusSevenDay);
	}
}