package com.boeingmerryho.business.ticketservice.application.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.ticketservice.application.user.dto.mapper.TicketApplicationMapper;
import com.boeingmerryho.business.ticketservice.application.user.dto.request.TicketByIdRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.request.TicketSearchRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.TicketSearchCriteria;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketRepository;
import com.boeingmerryho.business.ticketservice.domain.service.CreateTicketService;
import com.boeingmerryho.business.ticketservice.exception.ErrorCode;
import com.boeingmerryho.business.ticketservice.exception.TicketException;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatInfo;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatListenerDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final CreateTicketService createTicketService;
	private final TicketPaymentService ticketPaymentService;
	private final TicketApplicationMapper mapper;

	@Transactional(readOnly = true)
	public Page<TicketResponseServiceDto> getMyTickets(TicketSearchRequestServiceDto requestDto, Pageable pageable) {
		Page<Ticket> tickets = ticketRepository.findByCriteria(createTicketSearchCriteria(requestDto), pageable);

		return tickets.map(mapper::toTicketResponseDto);
	}

	@Transactional(readOnly = true)
	public TicketResponseServiceDto getTicketById(TicketByIdRequestServiceDto requestDto) {
		Ticket ticket = ticketRepository.findActiveTicketById(requestDto.id())
			.orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND));

		return mapper.toTicketResponseDto(ticket);
	}

	@Transactional
	public void handleSeatEvent(SeatListenerDto requestDto) {
		List<Ticket> tickets = createTicketService.createTickets(requestDto);
		// TODO : Redis 에 저장하기
		for (Ticket ticket : tickets) {
			ticketRepository.save(ticket);
		}

		List<SeatInfo> seats = requestDto.seatsInfo();
		ticketPaymentService.createPaymentForTickets(tickets, seats);
	}

	private TicketSearchCriteria createTicketSearchCriteria(TicketSearchRequestServiceDto requestDto) {
		return TicketSearchCriteria.builder()
			.matchId(requestDto.matchId())
			.seatId(requestDto.seatId())
			.userId(requestDto.userId())
			.ticketNo(requestDto.ticketNo())
			.status(requestDto.status())
			.isDeleted(Boolean.FALSE)
			.build();
	}
}

