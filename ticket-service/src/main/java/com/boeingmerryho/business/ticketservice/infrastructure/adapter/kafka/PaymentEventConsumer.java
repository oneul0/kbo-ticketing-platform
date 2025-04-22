package com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.ticketservice.application.user.TicketEventService;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.PaymentListenerDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

	private final TicketEventService ticketEventService;

	@KafkaListener(
		topics = "ticket-payment-process",
		groupId = "ticket-payment",
		containerFactory = "paymentKafkaListenerContainerFactory"
	)
	public void consume(PaymentListenerDto dto) {
		ticketEventService.handlePaymentEvent(dto);
	}
}
