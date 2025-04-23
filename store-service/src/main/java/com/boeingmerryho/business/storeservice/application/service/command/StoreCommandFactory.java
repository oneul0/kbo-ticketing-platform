package com.boeingmerryho.business.storeservice.application.service.command;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreCommandFactory {

	private final OpenStoreCommand openStoreCommand;
	private final CloseStoreCommand closeStoreCommand;

	public StoreCommand getCommand(StoreCommandType type) {
		return switch (type) {
			case OPEN -> openStoreCommand;
			case CLOSE -> closeStoreCommand;
		};
	}
}
