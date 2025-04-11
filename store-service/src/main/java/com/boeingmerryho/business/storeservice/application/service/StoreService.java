package com.boeingmerryho.business.storeservice.application.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.storeservice.application.dto.mapper.StoreApplicationMapper;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreSearchRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchResponseServiceDto;
import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.infrastructure.helper.StoreCommonHelper;
import com.boeingmerryho.business.storeservice.infrastructure.helper.StoreQueueAdminHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreApplicationMapper mapper;
	private final StoreCommonHelper storeCommonHelper;
	private final StoreQueueAdminHelper storeQueueAdminHelper;

	@Transactional(readOnly = true)
	public StoreDetailResponseServiceDto getStoreDetail(Long id) {
		Store storeDetail = storeCommonHelper.getActiveStoreById(id);

		boolean isQueueAvailable = storeQueueAdminHelper.isQueueAvailable(storeDetail.getId());
		return mapper.toStoreDetailResponseServiceDto(storeDetail, isQueueAvailable);
	}

	@Transactional(readOnly = true)
	public Page<StoreSearchResponseServiceDto> searchStore(
		StoreSearchRequestServiceDto requestServiceDto) {
		Page<Store> stores = storeCommonHelper.search(requestServiceDto);
		return stores.map(mapper::toStoreSearchResponseServiceDto);
	}
}
