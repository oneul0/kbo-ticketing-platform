package com.boeingmerryho.business.seatservice.application.service;

import org.springframework.stereotype.Service;

import com.boeingmerryho.business.seatservice.application.dto.mapper.SeatApplicationMapper;
import com.boeingmerryho.business.seatservice.application.dto.request.SeatCreateServiceRequestDto;
import com.boeingmerryho.business.seatservice.application.dto.response.SeatCreateServiceResponseDto;
import com.boeingmerryho.business.seatservice.domain.Seat;
import com.boeingmerryho.business.seatservice.infrastructure.SeatRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatAdminService {
	private final SeatRepository seatRepository;
	private final SeatApplicationMapper seatApplicationMapper;

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

	private String makeSeatNo(Integer seatBlock, Integer seatColumn, Integer seatRow) {
		return String.format("%02d", seatBlock) + String.format("%02d", seatColumn) + String.format("%02d", seatRow);
	}
}