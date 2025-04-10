package com.boeingmerryho.business.storeservice.domain.service;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.storeservice.domain.reader.StadiumReader;
import com.boeingmerryho.business.storeservice.domain.repository.StoreRepository;
import com.boeingmerryho.business.storeservice.exception.StoreErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreValidator {

	private final StoreRepository storeRepository;
	private final StadiumReader stadiumReader;

	public void validateNotDuplicated(Long stadiumId, String name) {
		if (!stadiumReader.existsById(stadiumId)) {
			throw new GlobalException(StoreErrorCode.INVALID_STADIUM);
		}

		if (storeRepository.existsByStadiumIdAndNameAndIsDeletedFalse(stadiumId, name)) {
			throw new GlobalException(StoreErrorCode.ALREADY_REGISTERED);
		}
	}
}
