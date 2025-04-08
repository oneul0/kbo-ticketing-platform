package com.boeingmerryho.business.storeservice.presentation.dto.mapper;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.storeservice.application.dto.request.StoreCreateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreCreateResponseServiceDto;
import com.boeingmerryho.business.storeservice.presentation.dto.request.StoreCreateRequestDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreCreateResponseDto;

@Mapper(componentModel = "spring")
public interface StorePresentationMapper {
	StoreCreateRequestServiceDto toStoreCreateRequestServiceDto(StoreCreateRequestDto dto);

	StoreCreateResponseDto toStoreCreateResponseDto(StoreCreateResponseServiceDto responseServiceDto);
}
