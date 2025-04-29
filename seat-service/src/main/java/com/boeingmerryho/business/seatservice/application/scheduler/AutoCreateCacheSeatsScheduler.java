package com.boeingmerryho.business.seatservice.application.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.domain.service.AutoCreateCacheSeatsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoCreateCacheSeatsScheduler {
	private final AutoCreateCacheSeatsService autoCreateCacheSeatsService;

	@Scheduled(cron = "0 00 17 * * MON-SUN")
	public void createCacheSeatsOnEveryDay() {
		autoCreateCacheSeatsService.preloadCacheSeats();
	}
}