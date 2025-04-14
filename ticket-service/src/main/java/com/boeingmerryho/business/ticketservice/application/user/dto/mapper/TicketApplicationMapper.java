package com.boeingmerryho.business.ticketservice.application.user.dto.mapper;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.domain.Ticket;

@Mapper(componentModel = "spring")
public interface TicketApplicationMapper {

	TicketResponseServiceDto toTicketResponseDto(Ticket ticket);
}
