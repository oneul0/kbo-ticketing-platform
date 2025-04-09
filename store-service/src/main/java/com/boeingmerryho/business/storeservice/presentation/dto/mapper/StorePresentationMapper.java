package com.boeingmerryho.business.storeservice.presentation.dto.mapper;

import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.storeservice.application.dto.request.StoreCreateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreSearchAdminRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreSearchRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.request.StoreUpdateRequestServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreCreateResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreDetailResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchAdminResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreSearchResponseServiceDto;
import com.boeingmerryho.business.storeservice.application.dto.response.StoreUpdateResponseServiceDto;
import com.boeingmerryho.business.storeservice.presentation.dto.request.StoreCreateRequestDto;
import com.boeingmerryho.business.storeservice.presentation.dto.request.StoreUpdateRequestDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreCreateResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreDetailAdminResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreDetailResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreSearchAdminResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreSearchResponseDto;
import com.boeingmerryho.business.storeservice.presentation.dto.response.StoreUpdateResponseDto;

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

	StoreSearchRequestServiceDto toStoreSearchRequestServiceDto(Pageable customPageable, Long stadiumId, String name,
		Boolean isClosed);

	StoreSearchResponseDto toStoreSearchResponseDto(StoreSearchResponseServiceDto storeSearchResponseServiceDto);

	StoreUpdateResponseDto toStoreUpdateResponseDto(StoreUpdateResponseServiceDto responseServiceDto);

	StoreUpdateRequestServiceDto toStoreUpdateRequestServiceDto(StoreUpdateRequestDto dto);
}
