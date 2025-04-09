package com.boeingmerryho.business.storeservice.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.storeservice.domain.entity.Store;

public interface StoreJpaRepository extends JpaRepository<Store, Long> {
	boolean existsByStadiumIdAndNameAndIsDeletedFalse(Long stadiumId, String name);

	Optional<Store> findByIdAndIsDeletedFalse(Long id);
}
