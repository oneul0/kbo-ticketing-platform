package com.boeingmerryho.business.seatservice.application.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.seatservice.domain.service.SeatFailedService;
import com.boeingmerryho.business.seatservice.domain.service.SeatSucceedService;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatListenerHelper;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatListenerResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
	private final SeatFailedService seatFailedService;
	private final SeatListenerHelper seatListenerHelper;
	private final SeatSucceedService seatSucceedService;

	@Transactional
	public void seatSucceed(SeatListenerResponseDto response) {
		LocalDate date = seatListenerHelper.parseLocalDate(response.matchDate());

		seatSucceedService.succeed(response.seatIds(), date);
	}

	@Transactional
	public void seatFailed(SeatListenerResponseDto response) {
		LocalDate date = seatListenerHelper.parseLocalDate(response.matchDate());

		seatFailedService.failed(response.seatIds(), date);
	}
}