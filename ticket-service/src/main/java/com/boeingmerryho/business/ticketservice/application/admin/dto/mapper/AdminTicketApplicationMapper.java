package com.boeingmerryho.business.ticketservice.application.admin.dto.mapper;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.ticketservice.application.admin.dto.response.AdminTicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.domain.Ticket;

@Mapper(componentModel = "spring")
public interface AdminTicketApplicationMapper {

	AdminTicketResponseServiceDto toAdminTicketResponseServiceDto(Ticket ticket);
}
