package com.boeingmerryho.business.seatservice.application.dto.mapper;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.seatservice.application.dto.response.SeatActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatCreateServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatInActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.domain.Seat;

@Mapper(componentModel = "spring")
public interface SeatApplicationMapper {
	SeatCreateServiceResponseDto toSeatCreateServiceResponseDto(Seat seat);

	SeatActiveServiceResponseDto toSeatActiveServiceResponseDto(Seat seat);

	SeatInActiveServiceResponseDto toSeatInActiveServiceResponseDto(Seat seat);
}