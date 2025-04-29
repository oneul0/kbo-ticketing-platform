package com.boeingmerryho.business.userservice.domain.event;

import com.boeingmerryho.business.userservice.domain.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginSuccessEvent {
	private final Long userId;
	private final User user;
}
