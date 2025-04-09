package com.boeingmerryho.business.storeservice.domain.repository;

import java.util.Optional;

import com.boeingmerryho.business.storeservice.domain.entity.Store;

public interface StoreRepository {
	Store save(Store store);

	boolean existsByStadiumIdAndNameAndIsDeletedFalse(Long stadiumId, String name);

	Optional<Store> findByIdAndIsDeletedFalse(Long id);
}
