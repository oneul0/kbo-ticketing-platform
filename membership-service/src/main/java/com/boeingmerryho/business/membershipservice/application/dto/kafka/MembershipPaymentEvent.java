package com.boeingmerryho.business.membershipservice.application.dto.kafka;

public record MembershipPaymentEvent(
	String event,
	Long userId,
	Long membershipId
) {
}