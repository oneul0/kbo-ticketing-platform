package com.boeingmerryho.business.seatservice.domain;

import lombok.Getter;

@Getter
public enum Membership {
	SVIP("SVIP"),
	VIP("VIP"),
	GOLD("GOLD"),
	SENIOR("SENIOR"),
	NORMAL("NORMAL");

	private final String description;

	Membership(String description) {
		this.description = description;
	}
}