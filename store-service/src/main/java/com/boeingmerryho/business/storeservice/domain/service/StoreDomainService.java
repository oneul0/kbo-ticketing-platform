package com.boeingmerryho.business.storeservice.domain.service;

import org.springframework.stereotype.Service;

import com.boeingmerryho.business.storeservice.application.dto.mapper.StoreApplicationMapper;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreCreateRequestServiceDto;
import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.domain.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreDomainService {

	private final StoreRepository storeRepository;
	private final StoreApplicationMapper mapper;

	public Store save(StoreCreateRequestServiceDto requestDto) {
		Store store = mapper.toEntity(requestDto);
		return storeRepository.save(store);
	}
}
