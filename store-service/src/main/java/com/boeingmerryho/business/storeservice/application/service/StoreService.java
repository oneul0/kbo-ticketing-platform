package com.boeingmerryho.business.storeservice.application.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.storeservice.application.dto.mapper.StoreApplicationMapper;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreSearchRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchResponseServiceDto;
import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.infrastructure.helper.StoreCommonHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreCommonHelper storeCommonHelper;
	private final StoreApplicationMapper mapper;

	public StoreDetailResponseServiceDto getStoreDetail(Long id) {
		Store storeDetail = storeCommonHelper.getActiveStoreById(id);
		return mapper.toStoreDetailResponseServiceDto(storeDetail);
	}

	public Page<StoreSearchResponseServiceDto> searchStore(
		StoreSearchRequestServiceDto requestServiceDto) {
		Page<Store> stores = storeCommonHelper.search(requestServiceDto);
		return stores.map(mapper::toStoreSearchResponseServiceDto);
	}
}
