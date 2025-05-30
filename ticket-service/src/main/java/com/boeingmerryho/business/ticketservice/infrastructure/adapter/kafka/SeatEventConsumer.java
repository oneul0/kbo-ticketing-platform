package com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.ticketservice.application.user.TicketSeatEventService;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatListenerDto;
import com.boeingmerryho.business.ticketservice.infrastructure.auditing.CustomAuditorAware;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatEventConsumer {

	private final TicketSeatEventService ticketEventService;

	@KafkaListener(
		topics = "ticket-created",
		groupId = "ticket-seat",
		containerFactory = "seatKafkaListenerContainerFactory"
	)
	@Counted(value = "ticket.created", description = "Number of ticket created events")
	@Timed(value = "ticket.processing.time", description = "Time taken to process ticket events")
	public void consume(SeatListenerDto dto) {
		try {
			Long userId = Long.parseLong(dto.seatsInfo().get(0).userId());
			CustomAuditorAware.setAuditor(userId);
			ticketEventService.handleSeatEvent(dto);
		} finally {
			CustomAuditorAware.clear();
		}
	}
}
