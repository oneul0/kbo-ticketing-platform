package com.boeingmerryho.business.ticketservice.application.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketRepository;
import com.boeingmerryho.business.ticketservice.domain.service.TicketFactory;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatInfo;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatListenerDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketSeatEventService {

	private final TicketFactory ticketFactory;
	private final TicketRepository ticketRepository;
	private final TicketPaymentService ticketPaymentService;

	@Transactional
	public void handleSeatEvent(SeatListenerDto requestDto) {
		List<Ticket> tickets = ticketFactory.createTickets(requestDto);
		// TODO : Redis 에 저장하기
		for (Ticket ticket : tickets) {
			ticketRepository.save(ticket);
		}

		List<SeatInfo> seats = requestDto.seatsInfo();
		ticketPaymentService.createPaymentForTickets(tickets, seats);
	}
}
