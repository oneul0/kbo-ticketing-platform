package com.boeingmerryho.business.ticketservice.presentation.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boeingmerryho.business.ticketservice.application.user.TicketService;
import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.presentation.TicketSuccessCode;
import com.boeingmerryho.business.ticketservice.presentation.user.dto.mapper.TicketPresentationMapper;
import com.boeingmerryho.business.ticketservice.presentation.user.dto.request.TicketSearchRequestDto;
import com.boeingmerryho.business.ticketservice.utils.PageableUtils;

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
		@RequestParam(value = "status", required = false) String status
	) {
		Pageable pageable = PageableUtils.customPageable(page, size, sortDirection, by);
		// TODO : Request Header 의 userId 사용하기
		Long userId = 1L;
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
}
