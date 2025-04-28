package com.boeingmerryho.business.seatservice.application.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void sendTicketCreatedEvent(ToTicketDto toTicketDto) {
		sendEvent("ticket-created", toTicketDto);
	}

	public <T> void sendEvent(String topic, T event) {
		kafkaTemplate.send(topic, event).whenComplete((result, ex) -> {
			if (ex != null) {
				log.error("[{}] 카프카 전송 실패", topic, ex);
			} else {
				log.info("[{}] 카프카 전송 성공: {}", topic, event);
			}
		});
	}
}