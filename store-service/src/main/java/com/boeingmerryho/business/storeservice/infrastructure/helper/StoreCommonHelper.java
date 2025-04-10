package com.boeingmerryho.business.storeservice.infrastructure.helper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.storeservice.application.dto.query.StoreSearchCondition;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreSearchRequestServiceDto;
import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.domain.repository.StoreQueryRepository;
import com.boeingmerryho.business.storeservice.domain.repository.StoreRepository;
import com.boeingmerryho.business.storeservice.exception.StoreErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreCommonHelper {

	private final StoreRepository storeRepository;
	private final StoreQueryRepository storeQueryRepository;

	public Store getActiveStoreById(Long id) {
		return storeRepository.findByIdAndIsDeletedFalse(id)
			.orElseThrow(() -> new GlobalException(StoreErrorCode.NOT_FOUND));
	}

	public Page<Store> search(StoreSearchRequestServiceDto requestServiceDto) {
		StoreSearchCondition condition = new StoreSearchCondition(requestServiceDto.stadiumId(),
			requestServiceDto.name(),
			requestServiceDto.isClosed(), false);
		return storeQueryRepository.search(condition, requestServiceDto.customPageable());
	}
}
