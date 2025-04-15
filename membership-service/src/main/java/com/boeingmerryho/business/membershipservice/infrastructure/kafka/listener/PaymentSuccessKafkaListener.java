package com.boeingmerryho.business.membershipservice.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.application.service.kafka.ListenerService;
import com.boeingmerryho.business.membershipservice.presentation.dto.response.MembershipPaymentEvent;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSuccessKafkaListener {

	private final ListenerService listenerService;

	@KafkaListener(topics = "membership-succeed", groupId = "membership-payment")
	public void membershipSucceed(MembershipPaymentEvent response) {
		try {
			listenerService.membershipSucceed(response);
		} catch (GlobalException e) {
			log.warn(e.getMessage());
		}
	}
}
