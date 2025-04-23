package com.boeingmerryho.business.storeservice.application.service.command;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.storeservice.infrastructure.helper.StoreAdminHelper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CloseStoreCommand implements StoreCommand {
	private final StoreAdminHelper storeAdminHelper;

	@Override
	public void execute(Long storeId) {
		storeAdminHelper.updateStoreClose(storeId);
	}
}