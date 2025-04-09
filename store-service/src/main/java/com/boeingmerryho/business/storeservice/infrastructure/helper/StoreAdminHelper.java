package com.boeingmerryho.business.storeservice.infrastructure.helper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.boeingmerryho.business.storeservice.application.dto.mapper.StoreApplicationMapper;
import com.boeingmerryho.business.storeservice.application.dto.query.StoreSearchCondition;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreCreateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreSearchAdminRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreUpdateRequestServiceDto;
import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.domain.repository.StoreQueryRepository;
import com.boeingmerryho.business.storeservice.domain.repository.StoreRepository;
import com.boeingmerryho.business.storeservice.exception.StoreErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreAdminHelper {

	private final StoreApplicationMapper mapper;
	private final StoreRepository storeRepository;
	private final StoreQueryRepository storeQueryRepository;

	public Store save(StoreCreateRequestServiceDto requestDto) {
		Store store = mapper.toEntity(requestDto);
		return storeRepository.save(store);
	}

	public Page<Store> search(StoreSearchAdminRequestServiceDto requestServiceDto) {
		StoreSearchCondition condition = new StoreSearchCondition(requestServiceDto.stadiumId(),
			requestServiceDto.name(),
			requestServiceDto.isClosed(), requestServiceDto.isDeleted());
		return storeQueryRepository.search(condition, requestServiceDto.customPageable());
	}

	public Store updateStoreInfo(Long id, StoreUpdateRequestServiceDto requestDto) {
		Store store = storeRepository.findByIdAndIsDeletedFalse(id)
			.orElseThrow(() -> new GlobalException(StoreErrorCode.NOT_FOUND));
		store.update(requestDto);

		return store;
	}

	public Store getAnyStoreById(Long id) {
		return storeRepository.findById(id)
			.orElseThrow(() -> new GlobalException(StoreErrorCode.NOT_FOUND));
	}
}
