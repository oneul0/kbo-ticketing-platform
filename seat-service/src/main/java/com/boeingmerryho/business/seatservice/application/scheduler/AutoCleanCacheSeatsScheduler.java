package com.boeingmerryho.business.seatservice.application.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.domain.service.AutoCleanCacheSeatsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoCleanCacheSeatsScheduler {
	private final AutoCleanCacheSeatsService autoCleanCacheSeatsService;

	@Scheduled(cron = "0 31 18 * * MON-FRI")
	public void cleanCacheSeatsOnWeekDay() {
		log.info("🧹 평일 자동 캐시 좌석 정리 시작");
		autoCleanCacheSeatsService.afterMatchStart();
	}

	@Scheduled(cron = "0 01 14 * * SAT,SUN")
	public void cleanCacheSeatsOnWeekend() {
		log.info("🧹 주말 자동 캐시 좌석 정리 시작");
		autoCleanCacheSeatsService.afterMatchStart();
	}
}