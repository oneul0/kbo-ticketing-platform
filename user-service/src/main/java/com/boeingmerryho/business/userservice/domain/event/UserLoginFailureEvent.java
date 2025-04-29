package com.boeingmerryho.business.userservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginFailureEvent {
	private final Long userId;
}