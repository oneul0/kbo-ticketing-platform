package com.boeingmerryho.business.paymentservice.application.dto.kafka;

public record MembershipPaymentEvent(
	String event,
	Long userId,
	Long membershipId
) {
}
