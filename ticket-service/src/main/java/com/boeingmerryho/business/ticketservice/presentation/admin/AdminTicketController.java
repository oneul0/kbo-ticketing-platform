package com.boeingmerryho.business.ticketservice.presentation.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.ticketservice.application.admin.AdminTicketService;
import com.boeingmerryho.business.ticketservice.application.admin.dto.response.AdminTicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.presentation.TicketSuccessCode;
import com.boeingmerryho.business.ticketservice.presentation.admin.dto.mapper.AdminTicketPresentationMapper;
import com.boeingmerryho.business.ticketservice.presentation.admin.dto.request.AdminTicketSearchRequestDto;
import com.boeingmerryho.business.ticketservice.utils.PageableUtils;

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

	@GetMapping
	public ResponseEntity<?> searchTickets(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "id", required = false) Long id,
		@RequestParam(value = "matchId", required = false) Long matchId,
		@RequestParam(value = "seatId", required = false) Long seatId,
		@RequestParam(value = "userId", required = false) Long userId,
		@RequestParam(value = "ticketNo", required = false) String ticketNo,
		@RequestParam(value = "status", required = false) String status,
		@RequestParam(value = "isDeleted", required = false) Boolean isDeleted
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);
		AdminTicketSearchRequestDto requestDto = new AdminTicketSearchRequestDto(
			id, matchId, seatId, userId, ticketNo, status, isDeleted
		);

		Page<AdminTicketResponseServiceDto> responseDto = ticketService
			.searchTickets(mapper.toAdminTicketSearchRequestServiceDto(requestDto), pageable);

		return SuccessResponse.of(
			TicketSuccessCode.TICKET_SEARCH,
			responseDto.map(mapper::toAdminTicketResponseDto)
		);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteTicket(@PathVariable Long id) {
		ticketService.softDeleteTicketById(mapper.toAdminTicketDeleteRequestServiceDto(id));
		return SuccessResponse.of(TicketSuccessCode.TICKET_DELETE);
	}
}
