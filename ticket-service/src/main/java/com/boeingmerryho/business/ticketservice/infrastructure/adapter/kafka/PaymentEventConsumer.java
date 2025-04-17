package com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.ticketservice.application.user.TicketService;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.PaymentListenerDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

	private final TicketService ticketService;

	@KafkaListener(
		topics = "ticket-payment-process",
		groupId = "ticket-payment",
		containerFactory = "paymentKafkaListenerContainerFactory"
	)
	public void consume(PaymentListenerDto dto) {
		ticketService.handlePaymentEvent(dto);
	}
}
