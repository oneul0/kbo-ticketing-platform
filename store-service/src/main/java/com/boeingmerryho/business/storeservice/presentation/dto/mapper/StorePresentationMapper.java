package com.boeingmerryho.business.storeservice.presentation.dto.mapper;

import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;

import com.boeingmerryho.business.storeservice.application.dto.request.StoreCreateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreCreateResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.presentation.dto.request.StoreCreateRequestDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreCreateResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreDetailAdminResponseDto;

@Primary
@Mapper(componentModel = "spring")
public interface StorePresentationMapper {
	StoreCreateRequestServiceDto toStoreCreateRequestServiceDto(StoreCreateRequestDto dto);

	StoreCreateResponseDto toStoreCreateResponseDto(StoreCreateResponseServiceDto responseServiceDto);

	StoreDetailAdminResponseDto toStoreDetailResponseDto(StoreDetailAdminResponseServiceDto responseServiceDto);
}
