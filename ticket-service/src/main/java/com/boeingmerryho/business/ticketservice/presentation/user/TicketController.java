package com.boeingmerryho.business.ticketservice.presentation.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.ticketservice.application.user.TicketService;
import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketPaymentResponseServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.presentation.TicketSuccessCode;
import com.boeingmerryho.business.ticketservice.presentation.user.dto.mapper.TicketPresentationMapper;
import com.boeingmerryho.business.ticketservice.presentation.user.dto.request.TicketSearchRequestDto;
import com.boeingmerryho.business.ticketservice.utils.PageableUtils;

import io.github.boeingmerryho.commonlibrary.interceptor.RequiredRoles;
import io.github.boeingmerryho.commonlibrary.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets")
public class TicketController {

	private final TicketService ticketService;
	private final TicketPresentationMapper mapper;

	@GetMapping("/me")
	public ResponseEntity<?> getMyTicket(
		@RequestParam(value = "page", required = false, defaultValue = "1") int page,
		@RequestParam(value = "size", required = false, defaultValue = "10") int size,
		@RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
		@RequestParam(value = "by", required = false) String by,
		@RequestParam(value = "matchId", required = false) Long matchId,
		@RequestParam(value = "seatId", required = false) Long seatId,
		@RequestParam(value = "ticketNo", required = false) String ticketNo,
		@RequestParam(value = "status", required = false) String status,
		@RequestAttribute("userId") Long userId
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);

		TicketSearchRequestDto requestDto = new TicketSearchRequestDto(
			matchId, seatId, userId, ticketNo, status
		);

		Page<TicketResponseServiceDto> responseDto = ticketService.getMyTickets(
			mapper.toTicketSearchRequestServiceDto(requestDto), pageable
		);

		return SuccessResponse.of(
			TicketSuccessCode.TICKET_SEARCH,
			responseDto.map(mapper::toTicketResponseDto)
		);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getTicketById(@PathVariable Long id) {
		// TODO : Request Header 의 userId 와 비교하기
		TicketResponseServiceDto responseDto = ticketService.getTicketById(
			mapper.toTicketByIdRequestServiceDto(id)
		);

		return SuccessResponse.of(
			TicketSuccessCode.TICKET_FOUND,
			mapper.toTicketResponseDto(responseDto)
		);
	}

	@GetMapping("/payments/me")
	public ResponseEntity<?> getMyTicketPayments(@RequestAttribute("userId") Long userId) {
		TicketPaymentResponseServiceDto responseDto = ticketService
			.getTicketPaymentInfo(mapper.toTicketPaymentResponseServiceDto(userId));

		return SuccessResponse.of(
			TicketSuccessCode.TICKET_PAYMENT_FOUND,
			mapper.toTicketPaymentResponseDto(responseDto)
		);
	}
}
