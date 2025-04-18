package com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.request.SeatListDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatKafkaProducer {

	private final KafkaTemplate<String, SeatListDto> kafkaTemplate;

	public void send(String topic, SeatListDto seatInfos) {
		kafkaTemplate.send(topic, seatInfos);
	}
}
