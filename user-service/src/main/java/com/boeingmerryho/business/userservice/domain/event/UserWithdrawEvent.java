package com.boeingmerryho.business.userservice.domain.event;

import lombok.Getter;

@Getter
public class UserWithdrawEvent {
	private final Long userId;

	public UserWithdrawEvent(Long userId) {
		this.userId = userId;
	}
}