package com.boeingmerryho.business.storeservice.application.service;

import org.springframework.stereotype.Service;

import com.boeingmerryho.business.storeservice.application.dto.mapper.StoreApplicationMapper;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreCreateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreCreateResponseServiceDto;
import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.domain.service.StoreDomainService;
import com.boeingmerryho.business.storeservice.domain.service.StoreValidator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreAdminService {

	private final StoreDomainService storeDomainService;
	private final StoreApplicationMapper mapper;
	private final StoreValidator validator;

	@Transactional
	public StoreCreateResponseServiceDto createStore(StoreCreateRequestServiceDto requestServiceDto) {
		validator.validateNotDuplicated(requestServiceDto.stadiumId(), requestServiceDto.name());
		Store saved = storeDomainService.save(requestServiceDto);
		return mapper.toStoreCreateResponseServiceDto(saved);
	}

}
