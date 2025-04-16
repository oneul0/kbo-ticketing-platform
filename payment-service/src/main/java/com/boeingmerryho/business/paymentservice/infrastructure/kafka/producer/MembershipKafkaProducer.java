package com.boeingmerryho.business.paymentservice.infrastructure.kafka.producer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.dto.kafka.MembershipPaymentEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MembershipKafkaProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void publishMembershipPayment(MembershipPaymentEvent successDto) {
		kafkaTemplate.send("payment-membership", successDto).thenAccept(
			result -> {
				RecordMetadata meta = result.getRecordMetadata();
				log.info("[Kafka Producing Success] - topic: {}, offset: {}, partition: {}",
					meta.topic(), meta.offset(), meta.partition());
			}
		);
	}

}