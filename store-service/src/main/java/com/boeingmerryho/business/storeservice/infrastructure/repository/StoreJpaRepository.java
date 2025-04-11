package com.boeingmerryho.business.storeservice.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.domain.repository.StoreRepository;

public interface StoreJpaRepository extends JpaRepository<Store, Long>, StoreRepository {
	boolean existsByStadiumIdAndNameAndIsDeletedFalse(Long stadiumId, String name);

	Optional<Store> findByIdAndIsDeletedFalse(Long id);

	List<Store> findAllByIsDeletedFalse();
}
