package com.boeingmerryho.business.storeservice.domain.repository;

import com.boeingmerryho.business.storeservice.domain.entity.Store;

public interface StoreRepository {
	Store save(Store store);

	boolean existsByStadiumIdAndNameAndIsDeletedFalse(Long stadiumId, String name);
}
