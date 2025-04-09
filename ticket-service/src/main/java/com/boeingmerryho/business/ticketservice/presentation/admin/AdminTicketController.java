package com.boeingmerryho.business.ticketservice.presentation.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.ticketservice.application.admin.AdminTicketService;
import com.boeingmerryho.business.ticketservice.application.admin.dto.response.AdminTicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.presentation.TicketSuccessCode;
import com.boeingmerryho.business.ticketservice.presentation.admin.dto.mapper.AdminTicketPresentationMapper;

import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/tickets")
public class AdminTicketController {

	private final AdminTicketService ticketService;
	private final AdminTicketPresentationMapper mapper;

	@GetMapping("/{id}")
	public ResponseEntity<?> getTicketById(@PathVariable Long id) {
		AdminTicketResponseServiceDto responseDto = ticketService
			.getTicketById(mapper.toAdminTicketIdRequestServiceDto(id));

		return SuccessResponse.of(
			TicketSuccessCode.TICKET_FOUND,
			mapper.toAdminTicketResponseDto(responseDto)
		);
	}
}
