package com.boeingmerryho.business.seatservice.application.dto.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.seatservice.application.dto.request.SeatServiceUpdateDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatUpdateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketMatchDto;
import com.boeingmerryho.business.seatservice.application.dto.request.ToTicketSeatDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheBlockServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheSeatServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatCreateServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatInActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatUpdateServiceResponseDto;
import com.boeingmerryho.business.seatservice.domain.Seat;

@Mapper(componentModel = "spring")
public interface SeatApplicationMapper {
	SeatCreateServiceResponseDto toSeatCreateServiceResponseDto(Seat seat);

	SeatActiveServiceResponseDto toSeatActiveServiceResponseDto(Seat seat);

	SeatInActiveServiceResponseDto toSeatInActiveServiceResponseDto(Seat seat);

	SeatServiceUpdateDto toSeatServiceUpdateDto(SeatUpdateServiceRequestDto seatUpdateServiceRequestDto);

	SeatUpdateServiceResponseDto toSeatUpdateServiceResponseDto(Seat seat);

	CacheSeatServiceResponseDto toCacheSeatServiceResponseDto(String seat, String status);

	CacheBlockServiceResponseDto toCacheBlockServiceResponseDto(Integer block, List<CacheSeatServiceResponseDto> seats);

	ToTicketSeatDto toTicketSeatDto(
		String id, String userId, String block, String column,
		String row, String price, String createdAt, String expiredAt
	);

	ToTicketDto toTicketDto(ToTicketMatchDto matchInfo, List<ToTicketSeatDto> seatsInfo);
}