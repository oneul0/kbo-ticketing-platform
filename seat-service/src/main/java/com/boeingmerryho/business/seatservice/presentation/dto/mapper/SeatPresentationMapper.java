package com.boeingmerryho.business.seatservice.presentation.dto.mapper;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.seatservice.application.dto.request.SeatCreateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatCreateServiceResponseDto;
import com.boeingmerryho.business.seatservice.presentation.dto.request.SeatCreateRequestDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatCreateResponseDto;

@Mapper(componentModel = "spring")
public interface SeatPresentationMapper {
	SeatCreateResponseDto toSeatCreateResponseDto(SeatCreateServiceResponseDto seatCreateServiceResponseDto);

	SeatCreateServiceRequestDto toSeatCreateServiceRequestDto(SeatCreateRequestDto seatCreateRequestDto);
}