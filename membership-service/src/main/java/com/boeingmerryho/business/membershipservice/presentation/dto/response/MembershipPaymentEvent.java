package com.boeingmerryho.business.membershipservice.presentation.dto.response;

public record MembershipPaymentEvent(
	String event,
	Long userId,
	Long membershipId
) {
}