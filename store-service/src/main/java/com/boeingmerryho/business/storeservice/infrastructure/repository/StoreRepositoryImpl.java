package com.boeingmerryho.business.storeservice.infrastructure.repository;

import com.boeingmerryho.business.storeservice.domain.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {
    private StoreJpaRepository storeJpaRepository;
}
