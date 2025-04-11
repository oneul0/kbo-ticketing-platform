package com.boeingmerryho.business.storeservice.infrastructure.helper;

import org.springframework.stereotype.Component;

import com.boeingmerryho.business.storeservice.application.dto.request.StoreUpdateRequestServiceDto;
import com.boeingmerryho.business.storeservice.domain.reader.StadiumReader;
import com.boeingmerryho.business.storeservice.domain.repository.StoreRepository;
import com.boeingmerryho.business.storeservice.exception.StoreErrorCode;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreValidator {

	private final StadiumReader stadiumReader;
	private final StoreRepository storeRepository;

	public void validateNotDuplicated(Long stadiumId, String name) {
		if (!stadiumReader.existsById(stadiumId)) {
			throw new GlobalException(StoreErrorCode.INVALID_STADIUM);
		}

		if (storeRepository.existsByStadiumIdAndNameAndIsDeletedFalse(stadiumId, name)) {
			throw new GlobalException(StoreErrorCode.ALREADY_REGISTERED);
		}
	}

	public void validateHasUpdatableFields(StoreUpdateRequestServiceDto dto) {
		if (dto.name() == null && dto.openAt() == null && dto.closedAt() == null) {
			throw new GlobalException(StoreErrorCode.NO_UPDATE_FIELDS_PROVIDED);
		}
	}
}
