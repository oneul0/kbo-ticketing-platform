package com.boeingmerryho.business.seatservice.application.dto.mapper;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.seatservice.application.dto.response.SeatCreateServiceResponseDto;
import com.boeingmerryho.business.seatservice.domain.Seat;

@Mapper(componentModel = "spring")
public interface SeatApplicationMapper {
	SeatCreateServiceResponseDto toSeatCreateServiceResponseDto(Seat seat);
}