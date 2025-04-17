package com.boeingmerryho.business.ticketservice.presentation.user.dto.mapper;

import org.mapstruct.Mapper;

import com.boeingmerryho.business.ticketservice.application.user.dto.request.TicketByIdRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.request.TicketPaymentRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.request.TicketSearchRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketPaymentResponseServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.presentation.user.dto.request.TicketSearchRequestDto;
import com.boeingmerryho.business.ticketservice.presentation.user.dto.response.TicketPaymentResponseDto;
import com.boeingmerryho.business.ticketservice.presentation.user.dto.response.TicketResponseDto;

@Mapper(componentModel = "spring")
public interface TicketPresentationMapper {

	TicketSearchRequestServiceDto toTicketSearchRequestServiceDto(TicketSearchRequestDto dto);

	TicketResponseDto toTicketResponseDto(TicketResponseServiceDto dto);

	TicketByIdRequestServiceDto toTicketByIdRequestServiceDto(Long id);

	TicketPaymentRequestServiceDto toTicketPaymentResponseServiceDto(Long userId);

	TicketPaymentResponseDto toTicketPaymentResponseDto(TicketPaymentResponseServiceDto dto);
}
