package com.boeingmerryho.business.seatservice.infrastructure.adaptor.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.application.service.KafkaListenerService;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatListenerResponseDto;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatSucceedListener {
	private final KafkaListenerService kafkaListenerService;

	@KafkaListener(topics = "seat-succeed", groupId = "seat-ticket")
	public void seatSucceedConsume(SeatListenerResponseDto response) {
		try {
			kafkaListenerService.seatSucceed(response);
		} catch (GlobalException e) {
			log.warn("GlobalException - {}", e.getMessage());
		}
	}
}