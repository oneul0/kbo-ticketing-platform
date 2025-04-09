package com.boeingmerryho.business.ticketservice.presentation.admin.dto.mapper;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.ticketservice.application.admin.dto.request.AdminTicketIdRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.admin.dto.response.AdminTicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.presentation.admin.dto.response.AdminTicketResponseDto;

@Mapper(componentModel = "spring")
public interface AdminTicketPresentationMapper {

	AdminTicketIdRequestServiceDto toAdminTicketIdRequestServiceDto(Long id);

	AdminTicketResponseDto toAdminTicketResponseDto(AdminTicketResponseServiceDto dto);
}
