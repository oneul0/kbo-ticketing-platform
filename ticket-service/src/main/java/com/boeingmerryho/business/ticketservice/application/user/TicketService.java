package com.boeingmerryho.business.ticketservice.application.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.ticketservice.application.user.dto.mapper.TicketApplicationMapper;
import com.boeingmerryho.business.ticketservice.application.user.dto.request.TicketSearchRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.TicketSearchCriteria;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final TicketApplicationMapper mapper;

	@Transactional(readOnly = true)
	public Page<TicketResponseServiceDto> getMyTickets(TicketSearchRequestServiceDto requestDto, Pageable pageable) {
		Page<Ticket> tickets = ticketRepository.findByCriteria(createTicketSearchCriteria(requestDto), pageable);

		return tickets.map(mapper::toTicketResponseDto);
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

