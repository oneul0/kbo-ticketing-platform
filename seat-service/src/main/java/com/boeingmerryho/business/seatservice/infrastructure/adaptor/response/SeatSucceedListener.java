package com.boeingmerryho.business.seatservice.infrastructure.adaptor.response;

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
public class SeatSucceedListener {
	private final ListenerService listenerService;

	@KafkaListener(topics = "seat-succeed", groupId = "seat-ticket")
	public void seatSucceedConsume(SeatListenerResponseDto response) {
		try {
			listenerService.seatSucceed(response);
		} catch (GlobalException e) {
			log.warn("GlobalException - {}", e.getMessage());
		}
	}
}