package com.boeingmerryho.business.ticketservice.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.TicketStatus;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.MatchInfo;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatInfo;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatListenerDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTicketService {

	public List<Ticket> createTickets(SeatListenerDto requestDto) {
		MatchInfo matchInfo = requestDto.matchInfo();
		return requestDto.seatsInfo().stream()
			.map(seat -> {
				String ticketNo = generateTicketNo(matchInfo, seat);
				return Ticket.builder()
					.matchId(Long.parseLong(matchInfo.id()))
					.seatId(Long.parseLong(seat.id()))
					.userId(Long.parseLong(seat.userId()))
					.ticketNo(ticketNo)
					.status(TicketStatus.PENDING)
					.build();
			})
			.toList();
	}

	private String generateTicketNo(MatchInfo matchInfo, SeatInfo seatInfo) {
		return String.format("%s-%s-%s-%s",
			matchInfo.matchDay().replaceAll("-", ""), matchInfo.stadiumId(), matchInfo.id(),
			String.format("%s%02d%02d", seatInfo.block(), Integer.parseInt(seatInfo.column()), Integer.parseInt(seatInfo.row())));
	}
}
