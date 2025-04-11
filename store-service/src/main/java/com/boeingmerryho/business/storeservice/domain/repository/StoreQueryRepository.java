package com.boeingmerryho.business.storeservice.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.storeservice.application.dto.query.StoreSearchCondition;
import com.boeingmerryho.business.storeservice.domain.entity.Store;

public interface StoreQueryRepository {
	Page<Store> search(StoreSearchCondition condition, Pageable pageable);

}
