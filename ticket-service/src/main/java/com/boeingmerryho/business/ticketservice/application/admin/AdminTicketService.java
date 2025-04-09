package com.boeingmerryho.business.ticketservice.application.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.ticketservice.application.admin.dto.mapper.AdminTicketApplicationMapper;
import com.boeingmerryho.business.ticketservice.application.admin.dto.request.AdminTicketIdRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.admin.dto.response.AdminTicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.domain.Ticket;
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
}
