package com.boeingmerryho.business.storeservice.application.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;

import com.boeingmerryho.business.storeservice.application.dto.request.StoreCreateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreCreateResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchResponseServiceDto;
import com.boeingmerryho.business.storeservice.domain.entity.Store;

@Primary
@Mapper(componentModel = "spring")
public interface StoreApplicationMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "updatedBy", ignore = true)
	@Mapping(target = "isDeleted", ignore = true)
	@Mapping(target = "deletedBy", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	Store toEntity(StoreCreateRequestServiceDto requestServiceDto);

	@BeanMapping(ignoreByDefault = true)
	StoreCreateResponseServiceDto toStoreCreateResponseServiceDto(Store savedStore);

	StoreDetailAdminResponseServiceDto toStoreDetailAdminResponseServiceDto(Store storeDetail);

	StoreDetailResponseServiceDto toStoreDetailResponseServiceDto(Store storeDetail);

	StoreSearchAdminResponseServiceDto toStoreSearchAdminResponseServiceDto(Store store);

	StoreSearchResponseServiceDto toStoreSearchResponseServiceDto(Store store);
}
