package com.boeingmerryho.business.seatservice.application.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.seatservice.application.dto.mapper.SeatApplicationMapper;
import com.boeingmerryho.business.seatservice.application.dto.request.CacheSeatCreateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatActiveServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatCreateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatDeleteServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatInActiveServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatServiceUpdateDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatUpdateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatCreateServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatInActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatUpdateServiceResponseDto;
import com.boeingmerryho.business.seatservice.domain.Seat;
import com.boeingmerryho.business.seatservice.domain.service.CreateCacheSeatsService;
import com.boeingmerryho.business.seatservice.exception.SeatErrorCode;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatAdminServiceHelper;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatCommonHelper;

import io.github.boeingmerryho.commonlibrary.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatAdminService {
	private final SeatCommonHelper seatCommonHelper;
	private final SeatApplicationMapper seatApplicationMapper;
	private final SeatAdminServiceHelper seatAdminServiceHelper;
	private final CreateCacheSeatsService createCacheSeatsService;

	@Transactional
	public SeatCreateServiceResponseDto createSeat(SeatCreateServiceRequestDto serviceCreate) {
		String newSeatNo = makeSeatNo(serviceCreate.seatBlock(), serviceCreate.seatColumn(), serviceCreate.seatRow());

		Seat newSeat = Seat.builder()
			.name(serviceCreate.name())
			.seatBlock(serviceCreate.seatBlock())
			.seatColumn(serviceCreate.seatColumn())
			.seatRow(serviceCreate.seatRow())
			.seatNo(newSeatNo)
			.price(serviceCreate.price())
			.isActive(serviceCreate.isActive())
			.build();

		seatCommonHelper.save(newSeat);
		log.info("seatNo: {}, 새로운 좌석 생성 완료", newSeatNo);

		return seatApplicationMapper.toSeatCreateServiceResponseDto(newSeat);
	}

	@Transactional
	public SeatActiveServiceResponseDto activeSeat(SeatActiveServiceRequestDto serviceDto) {
		Seat seat = seatCommonHelper.getSeatById(serviceDto.id());

		seat.active();
		log.info("seatNo: {}, 좌석 이용 가능 상태로 변경 완료", seat.getSeatNo());

		return seatApplicationMapper.toSeatActiveServiceResponseDto(seat);
	}

	@Transactional
	public SeatInActiveServiceResponseDto inActiveSeat(SeatInActiveServiceRequestDto serviceDto) {
		Seat seat = seatCommonHelper.getSeatById(serviceDto.id());

		seat.inActive();
		log.info("seatNo: {}, 좌석 이용 불가능 상태로 변경 완료", seat.getSeatNo());

		return seatApplicationMapper.toSeatInActiveServiceResponseDto(seat);
	}

	@Transactional
	public SeatUpdateServiceResponseDto updateSeat(SeatUpdateServiceRequestDto serviceDto) {
		Seat seat = seatCommonHelper.getSeatById(serviceDto.id());

		SeatServiceUpdateDto seatServiceUpdateDto = seatApplicationMapper.toSeatServiceUpdateDto(serviceDto);

		seat.update(seatServiceUpdateDto);
		log.info("seatId: {}, 좌석 정보 변경 완료", seat.getId());

		return seatApplicationMapper.toSeatUpdateServiceResponseDto(seat);
	}

	@Transactional
	public void deleteSeat(SeatDeleteServiceRequestDto serviceDto) {
		Seat seat = seatCommonHelper.getSeatById(serviceDto.id());
		seatAdminServiceHelper.delete(seat);

		log.info("seatId: {}, 좌석 삭제 완료", seat.getId());
	}

	@Transactional
	public void createCacheSeats(CacheSeatCreateServiceRequestDto create) {
		if (create.date().isBefore(LocalDate.now())) {
			throw new GlobalException(SeatErrorCode.INVALID_ACCESS);
		}

		List<Seat> seats = seatAdminServiceHelper.getSeatsByIsActiveIsTrue();
		createCacheSeatsService.createSeatBucket(seats, create.date());

		log.info("날짜: {} - 좌석 생성 완료", create.date());
	}

	private String makeSeatNo(Integer seatBlock, Integer seatColumn, Integer seatRow) {
		return String.format("%02d", seatBlock) + String.format("%02d", seatColumn) + String.format("%02d", seatRow);
	}
}