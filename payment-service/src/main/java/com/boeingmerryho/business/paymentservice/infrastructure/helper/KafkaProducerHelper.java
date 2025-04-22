package com.boeingmerryho.business.paymentservice.infrastructure.helper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.dto.kafka.MembershipPaymentEvent;
import com.boeingmerryho.business.paymentservice.application.dto.kafka.TicketPaymentEvent;
import com.boeingmerryho.business.paymentservice.infrastructure.kafka.producer.MembershipKafkaProducer;
import com.boeingmerryho.business.paymentservice.infrastructure.kafka.producer.TicketKafkaProducer;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaProducerHelper {

	private final String SUCCESS_PREFIX = "success";
	private final String FAILURE_PREFIX = "fail";
	private final TicketKafkaProducer ticketKafkaProducer;
	private final MembershipKafkaProducer membershipKafkaProducer;

	public void publishMembershipPaymentSuccess(Long userId, Long membershipId) {
		membershipKafkaProducer.publishMembershipPayment(new MembershipPaymentEvent(
			SUCCESS_PREFIX,
			userId,
			membershipId
		));
	}

	public void publishMembershipPaymentFailure(Long userId, Long membeshipId) {
		membershipKafkaProducer.publishMembershipPayment(new MembershipPaymentEvent(
			FAILURE_PREFIX,
			userId,
			membeshipId
		));
	}

	public void publishTicketPaymentSuccess(List<String> tickets) {
		ticketKafkaProducer.publishTicketPayment(new TicketPaymentEvent(
			SUCCESS_PREFIX,
			tickets
		));
	}

	public void publishTicketPaymentFailure(List<String> tickets) {
		ticketKafkaProducer.publishTicketPayment(new TicketPaymentEvent(
			FAILURE_PREFIX,
			tickets
		));
	}
}
