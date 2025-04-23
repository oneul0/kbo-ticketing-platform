package com.boeingmerryho.business.storeservice.application.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.storeservice.application.dto.mapper.StoreApplicationMapper;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreCreateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreSearchAdminRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreUpdateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreCreateResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreUpdateResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.service.command.StoreCommandFactory;
import com.boeingmerryho.business.storeservice.application.service.command.StoreCommandType;
import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.infrastructure.helper.StoreAdminHelper;
import com.boeingmerryho.business.storeservice.infrastructure.helper.StoreQueueAdminHelper;
import com.boeingmerryho.business.storeservice.infrastructure.helper.StoreValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreAdminService {

	private final StoreValidator validator;
	private final StoreApplicationMapper mapper;
	private final StoreAdminHelper storeAdminHelper;
	private final StoreCommandFactory storeCommandFactory;
	private final StoreQueueAdminHelper storeQueueAdminHelper;

	@Transactional
	public StoreCreateResponseServiceDto createStore(StoreCreateRequestServiceDto requestServiceDto) {
		validator.validateNotDuplicated(requestServiceDto.stadiumId(), requestServiceDto.name());

		Store saved = storeAdminHelper.save(requestServiceDto);
		return mapper.toStoreCreateResponseServiceDto(saved);
	}

	@Transactional(readOnly = true)
	public StoreDetailAdminResponseServiceDto getStoreDetail(Long id) {
		Store storeDetail = storeAdminHelper.getAnyStoreById(id);

		boolean isQueueAvailable = storeQueueAdminHelper.isQueueAvailable(storeDetail.getId());

		return mapper.toStoreDetailAdminResponseServiceDto(storeDetail, isQueueAvailable);
	}

	@Transactional(readOnly = true)
	public Page<StoreSearchAdminResponseServiceDto> searchStore(
		StoreSearchAdminRequestServiceDto requestServiceDto) {
		Page<Store> stores = storeAdminHelper.search(requestServiceDto);
		return stores.map(mapper::toStoreSearchAdminResponseServiceDto);
	}

	@Transactional
	public StoreUpdateResponseServiceDto updateStore(Long id, StoreUpdateRequestServiceDto requestDto) {
		validator.validateHasUpdatableFields(requestDto);
		Store updated = storeAdminHelper.updateStoreInfo(id, requestDto);
		return mapper.toStoreUpdateResponseServiceDto(updated);
	}

	@Transactional
	public StoreUpdateResponseServiceDto changeStoreStatus(Long id, StoreCommandType type) {
		storeCommandFactory.getCommand(type).execute(id);
		Store store = storeAdminHelper.getAnyStoreById(id);
		return mapper.toStoreUpdateResponseServiceDto(store);
	}

	public void enableQueue(Long id) {
		storeQueueAdminHelper.enableQueue(id);
	}

	public void disableQueue(Long id) {
		storeQueueAdminHelper.disableQueue(id);
	}

	@Transactional
	public void deleteStore(Long id) {
		storeAdminHelper.deleteStore(id);
	}
}
