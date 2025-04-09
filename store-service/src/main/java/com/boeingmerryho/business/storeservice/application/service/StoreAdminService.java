package com.boeingmerryho.business.storeservice.application.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.storeservice.application.dto.mapper.StoreApplicationMapper;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreCreateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreSearchAdminRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreUpdateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreCreateResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreUpdateResponseServiceDto;
import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.infrastructure.helper.StoreAdminHelper;
import com.boeingmerryho.business.storeservice.infrastructure.helper.StoreValidator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreAdminService {

	private final StoreAdminHelper storeAdminHelper;
	private final StoreApplicationMapper mapper;
	private final StoreValidator validator;

	@Transactional
	public StoreCreateResponseServiceDto createStore(StoreCreateRequestServiceDto requestServiceDto) {
		validator.validateNotDuplicated(requestServiceDto.stadiumId(), requestServiceDto.name());

		Store saved = storeAdminHelper.save(requestServiceDto);
		return mapper.toStoreCreateResponseServiceDto(saved);
	}

	public StoreDetailAdminResponseServiceDto getStoreDetail(Long id) {
		Store storeDetail = storeAdminHelper.getAnyStoreById(id);
		return mapper.toStoreDetailAdminResponseServiceDto(storeDetail);
	}

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
}
