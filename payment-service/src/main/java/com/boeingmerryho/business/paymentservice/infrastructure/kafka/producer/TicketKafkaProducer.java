package com.boeingmerryho.business.paymentservice.infrastructure.kafka.producer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.dto.kafka.TicketPaymentEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketKafkaProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void publishTicketPayment(TicketPaymentEvent successDto) {
		kafkaTemplate.send("ticket-payment-process", successDto).thenAccept(
			result -> {
				RecordMetadata meta = result.getRecordMetadata();
				log.info("[Kafka Producing Success] - topic: {}, offset: {}, partition: {}",
					meta.topic(), meta.offset(), meta.partition());
			}
		);
	}
}
