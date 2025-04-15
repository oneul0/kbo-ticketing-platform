package com.boeingmerryho.business.membershipservice.infrastructure.kafka.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.membershipservice.application.dto.kafka.MembershipPaymentEvent;
import com.boeingmerryho.business.membershipservice.application.service.kafka.ListenerService;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSuccessKafkaListener {

	private final ListenerService listenerService;

	@KafkaListener(topics = "payment-membership-process", groupId = "membership-service")
	public void membershipSucceed(MembershipPaymentEvent response) {
		try {
			switch (response.event()) {
				case "success" -> listenerService.paymentSucceed(response);
				case "fail" -> listenerService.paymentFailure(response);
				case "cancel" -> listenerService.paymentCancel(response);
				default -> log.warn("알 수 없는 이벤트 타입: {}", response.event());
			}
		} catch (GlobalException e) {
			log.warn(e.getMessage());
		}
	}
}
