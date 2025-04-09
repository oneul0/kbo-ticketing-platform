package com.boeingmerryho.business.seatservice.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.seatservice.application.dto.mapper.SeatApplicationMapper;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatActiveServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatCreateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatInActiveServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatCreateServiceResponseDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatInActiveServiceResponseDto;
import com.boeingmerryho.business.seatservice.domain.Seat;
import com.boeingmerryho.business.seatservice.infrastructure.SeatRepository;
import com.boeingmerryho.business.seatservice.infrastructure.helper.SeatServiceHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatAdminService {
	private final SeatRepository seatRepository;
	private final SeatServiceHelper seatServiceHelper;
	private final SeatApplicationMapper seatApplicationMapper;

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

		seatRepository.save(newSeat);
		log.info("seatNo: {}, 새로운 좌석 생성 완료", newSeatNo);

		return seatApplicationMapper.toSeatCreateServiceResponseDto(newSeat);
	}

	@Transactional
	public SeatActiveServiceResponseDto activeSeat(SeatActiveServiceRequestDto serviceDto) {
		Seat seat = seatServiceHelper.getSeatById(serviceDto.id());

		seat.active();
		log.info("seatNo: {}, 좌석 이용 가능 상태로 변경 완료", seat.getSeatNo());

		return seatApplicationMapper.toSeatActiveServiceResponseDto(seat);
	}

	@Transactional
	public SeatInActiveServiceResponseDto inActiveSeat(SeatInActiveServiceRequestDto serviceDto) {
		Seat seat = seatServiceHelper.getSeatById(serviceDto.id());

		seat.inActive();
		log.info("seatNo: {}, 좌석 이용 불가능 상태로 변경 완료", seat.getSeatNo());

		return seatApplicationMapper.toSeatInActiveServiceResponseDto(seat);
	}

	private String makeSeatNo(Integer seatBlock, Integer seatColumn, Integer seatRow) {
		return String.format("%02d", seatBlock) + String.format("%02d", seatColumn) + String.format("%02d", seatRow);
	}
}