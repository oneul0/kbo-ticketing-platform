package com.boeingmerryho.business.storeservice.domain.service;

import org.springframework.stereotype.Service;

import com.boeingmerryho.business.storeservice.application.dto.mapper.StoreApplicationMapper;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreCreateRequestServiceDto;
import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.domain.repository.StoreRepository;
import com.boeingmerryho.business.storeservice.exception.StoreErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
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

	public Store findById(Long id) {
		return storeRepository.findByIdAndIsDeletedFalse(id)
			.orElseThrow(() -> new GlobalException(StoreErrorCode.NOT_FOUND));
	}
}
