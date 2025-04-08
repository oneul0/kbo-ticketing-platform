package com.boeingmerryho.business.storeservice.infrastructure.repository;

import com.boeingmerryho.business.storeservice.domain.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreJpaRepository extends JpaRepository<Store, Long> {
}
