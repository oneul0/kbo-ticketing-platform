package com.boeingmerryho.business.storeservice.application.service;

import org.springframework.stereotype.Service;

import com.boeingmerryho.business.storeservice.application.dto.mapper.StoreApplicationMapper;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailResponseServiceDto;
import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.domain.service.StoreDomainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreDomainService storeDomainService;
	private final StoreApplicationMapper mapper;

	public StoreDetailResponseServiceDto getStoreDetail(Long id) {
		Store storeDetail = storeDomainService.getActiveStoreById(id);
		return mapper.toStoreDetailResponseServiceDto(storeDetail);
	}
}
