package com.boeingmerryho.business.membershipservice.domain.type;

import lombok.Getter;

@Getter
public enum MembershipType {
	SENIOR("Senior - Level0"),
	GOLD("Gold - Level1"),
	VIP("VIP - Level2"),
	SVIP("SVIP - Level3"),
	;

	private final String description;

	MembershipType(String description) {
		this.description = description;
	}
}
