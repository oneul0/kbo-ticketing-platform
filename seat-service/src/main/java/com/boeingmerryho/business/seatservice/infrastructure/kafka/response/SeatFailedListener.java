package com.boeingmerryho.business.seatservice.infrastructure.kafka.response;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.application.service.ListenerService;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatListenerResponseDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatFailedListener {
	private final ListenerService listenerService;

	@KafkaListener(topics = "seat-failed", groupId = "seat-ticket")
	public void seatFailedConsume(SeatListenerResponseDto response) {
		try {
			listenerService.seatFailed(response);
		} catch (GlobalException e) {
			log.warn("GlobalException - {}", e.getMessage());
		}
	}
}