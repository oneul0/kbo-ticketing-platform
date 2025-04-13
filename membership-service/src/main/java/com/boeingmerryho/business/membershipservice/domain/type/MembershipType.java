package com.boeingmerryho.business.membershipservice.domain.type;

import lombok.Getter;

@Getter
public enum MembershipType {
	SENIOR("Senior - Level0", 0),
	GOLD("Gold - Level1", 40),
	VIP("VIP - Level2", 70),
	SVIP("SVIP - Level3", 100);

	private final String description;
	private final int initialQuantity;

	MembershipType(String description, int initialQuantity) {
		this.description = description;
		this.initialQuantity = initialQuantity;
	}
}
