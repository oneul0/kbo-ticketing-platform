package com.boeingmerryho.business.seatservice.presentation.dto.mapper;

import java.time.LocalDate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.boeingmerryho.business.seatservice.application.dto.request.CacheBlockServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatCreateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatActiveServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatCreateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatDeleteServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatInActiveServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatUpdateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.CacheBlockServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatCreateServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatInActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatUpdateServiceResponseDto;
import com.boeingmerryho.business.seatservice.presentation.dto.request.SeatCreateRequestDto;
import com.boeingmerryho.business.seatservice.presentation.dto.request.SeatUpdateRequestDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.CacheBlockResponseDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatCreateResponseDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatResponseDto;
import com.boeingmerryho.business.seatservice.presentation.dto.response.SeatUpdateResponseDto;

@Mapper(componentModel = "spring")
public interface SeatPresentationMapper {
	SeatCreateResponseDto toSeatCreateResponseDto(SeatCreateServiceResponseDto seatCreateServiceResponseDto);

	SeatCreateServiceRequestDto toSeatCreateServiceRequestDto(SeatCreateRequestDto seatCreateRequestDto);

	SeatActiveServiceRequestDto toSeatActiveServiceRequestDto(Long id);

	SeatResponseDto toSeatResponseDtoForActive(SeatActiveServiceResponseDto seatActiveServiceResponseDto);

	SeatInActiveServiceRequestDto toSeatInActiveServiceRequestDto(Long id);

	SeatResponseDto toSeatResponseDtoForInActive(SeatInActiveServiceResponseDto seatInActiveServiceResponseDto);

	SeatUpdateServiceRequestDto toSeatUpdateServiceRequestDto(Long id, SeatUpdateRequestDto seatUpdateRequestDto);

	SeatUpdateResponseDto toSeatUpdateResponseDto(SeatUpdateServiceResponseDto seatUpdateServiceResponseDto);

	SeatDeleteServiceRequestDto toSeatDeleteServiceRequestDto(Long id);

	CacheSeatCreateServiceRequestDto toCacheSeatCreateServiceRequestDto(LocalDate date);

	CacheBlockServiceRequestDto toCacheBlockServiceRequestDto(Integer blockId, LocalDate date);

	@Mapping(target = "block", source = "cacheBlockServiceResponseDto.block")
	@Mapping(target = "seats", source = "cacheBlockServiceResponseDto.seats")
	CacheBlockResponseDto toCacheBlockResponseDto(CacheBlockServiceResponseDto cacheBlockServiceResponseDto);
}