package com.boeingmerryho.business.ticketservice.domain;

public enum PaymentStatus {
	SUCCESS, FAIL;

	public static PaymentStatus from(String status) {
		return switch (status) {
			case "success" -> SUCCESS;
			case "fail" -> FAIL;
			default -> throw new IllegalArgumentException("Unknown payment status: " + status);
		};
	}
}
