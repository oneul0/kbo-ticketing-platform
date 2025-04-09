package com.boeingmerryho.business.storeservice.presentation.dto.mapper;

import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.storeservice.application.dto.request.StoreCreateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreSearchAdminRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreCreateResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.presentation.dto.request.StoreCreateRequestDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreCreateResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreDetailAdminResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreDetailResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreSearchAdminResponseDto;

@Primary
@Mapper(componentModel = "spring")
public interface StorePresentationMapper {
	StoreCreateRequestServiceDto toStoreCreateRequestServiceDto(StoreCreateRequestDto dto);

	StoreCreateResponseDto toStoreCreateResponseDto(StoreCreateResponseServiceDto responseServiceDto);

	StoreDetailAdminResponseDto toStoreDetailAdminResponseDto(StoreDetailAdminResponseServiceDto responseServiceDto);

	StoreDetailResponseDto toStoreDetailResponseDto(StoreDetailResponseServiceDto responseServiceDto);

	StoreSearchAdminRequestServiceDto toStoreSearchAdminRequestServiceDto(
		Pageable customPageable,
		Long stadiumId,
		String name,
		Boolean isClosed,
		Boolean isDeleted);

	StoreSearchAdminResponseDto toStoreSearchAdminResponseDto(
		StoreSearchAdminResponseServiceDto storeSearchAdminResponseServiceDto);
}
