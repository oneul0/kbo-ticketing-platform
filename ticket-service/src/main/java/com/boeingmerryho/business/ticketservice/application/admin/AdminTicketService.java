package com.boeingmerryho.business.ticketservice.application.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.ticketservice.application.admin.dto.mapper.AdminTicketApplicationMapper;
import com.boeingmerryho.business.ticketservice.application.admin.dto.request.AdminTicketDeleteRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.admin.dto.request.AdminTicketIdRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.admin.dto.request.AdminTicketSearchRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.admin.dto.request.AdminTicketStatusUpdateRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.admin.dto.response.AdminTicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.TicketSearchCriteria;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketRepository;
import com.boeingmerryho.business.ticketservice.exception.ErrorCode;
import com.boeingmerryho.business.ticketservice.exception.TicketException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminTicketService {

	private final TicketRepository ticketRepository;
	private final AdminTicketApplicationMapper mapper;

	@Transactional(readOnly = true)
	public AdminTicketResponseServiceDto getTicketById(AdminTicketIdRequestServiceDto requestDto) {
		Ticket ticket = ticketRepository.findById(requestDto.id())
			.orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND));
		return mapper.toAdminTicketResponseServiceDto(ticket);
	}

	@Transactional(readOnly = true)
	public Page<AdminTicketResponseServiceDto> searchTickets(
		AdminTicketSearchRequestServiceDto requestDto,
		Pageable pageable
	) {
		Page<Ticket> tickets = ticketRepository.findByCriteria(createTicketSearchCriteria(requestDto), pageable);

		return tickets.map(mapper::toAdminTicketResponseServiceDto);
	}

	private TicketSearchCriteria createTicketSearchCriteria(AdminTicketSearchRequestServiceDto requestDto) {
		return TicketSearchCriteria.builder()
			.id(requestDto.id())
			.matchId(requestDto.matchId())
			.seatId(requestDto.seatId())
			.userId(requestDto.userId())
			.ticketNo(requestDto.ticketNo())
			.status(requestDto.status())
			.isDeleted(requestDto.isDeleted())
			.build();
	}

	@Transactional
	public AdminTicketResponseServiceDto updateTicketStatus(
		Long id,
		AdminTicketStatusUpdateRequestServiceDto requestDto
	) {
		Ticket ticket = ticketRepository.findById(id)
			.orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND));

		ticket.updateStatus(requestDto.status());

		return mapper.toAdminTicketResponseServiceDto(ticket);
	}

	@Transactional
	public void softDeleteTicketById(AdminTicketDeleteRequestServiceDto requestDto) {
		Ticket ticket = ticketRepository.findById(requestDto.id())
			.orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND));

		// TODO : request Header 에서 사용자 Id 정보 받기
		ticket.softDelete(null);
	}
}
