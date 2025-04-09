package com.boeingmerryho.business.storeservice.domain.repository;

import org.springframework.data.domain.Page;

import com.boeingmerryho.business.storeservice.application.dto.request.StoreSearchAdminRequestServiceDto;
import com.boeingmerryho.business.storeservice.domain.entity.Store;

public interface StoreQueryRepository {
	Page<Store> search(StoreSearchAdminRequestServiceDto requestServiceDto);
}
