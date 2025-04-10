package com.boeingmerryho.business.seatservice.domain;

import lombok.Getter;

@Getter
public enum ReservationStatus {
	AVAILABLE("예약 가능한 좌석"),
	PROCESSING("예약 처리 중인 좌석"),
	COMPLETED("예약 완료된 좌석");

	private final String description;

	ReservationStatus(String description) {
		this.description = description;
	}
}