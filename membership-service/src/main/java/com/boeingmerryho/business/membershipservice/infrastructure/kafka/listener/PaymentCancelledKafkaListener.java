package com.boeingmerryho.business.membershipservice.infrastructure.kafka.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.application.service.kafka.ListenerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCancelledKafkaListener {

	private final ListenerService listenerService;

	@KafkaListener(topics = "membership-cancelled", groupId = "membership-payment")
	public void membershipCancelled(ConsumerRecord<String, String> record) {

	}

}
