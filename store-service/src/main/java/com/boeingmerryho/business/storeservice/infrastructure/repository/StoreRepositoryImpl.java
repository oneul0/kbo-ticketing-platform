package com.boeingmerryho.business.storeservice.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.domain.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {
	private final StoreJpaRepository storeJpaRepository;

	@Override
	public Store save(Store store) {
		return storeJpaRepository.save(store);
	}

	@Override
	public boolean existsByStadiumIdAndNameAndIsDeletedFalse(Long stadiumId, String name) {
		return storeJpaRepository.existsByStadiumIdAndNameAndIsDeletedFalse(stadiumId, name);
	}

	@Override
	public Optional<Store> findByIdAndIsDeletedFalse(Long id) {
		return storeJpaRepository.findByIdAndIsDeletedFalse(id);
	}
}
