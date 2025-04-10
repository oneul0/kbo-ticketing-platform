package com.boeingmerryho.business.ticketservice.application.admin;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.boeingmerryho.business.ticketservice.application.admin.dto.mapper.AdminTicketApplicationMapper;
import com.boeingmerryho.business.ticketservice.application.admin.dto.request.AdminTicketIdRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.admin.dto.response.AdminTicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.TicketStatus;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketRepository;

@ExtendWith(MockitoExtension.class)
class AdminTicketServiceTest {

	@InjectMocks
	private AdminTicketService adminTicketService;

	@Mock
	private TicketRepository ticketRepository;

	@Mock
	private AdminTicketApplicationMapper mapper;

	@DisplayName("관리자는 티켓 ID를 통해 티켓을 조회할 수 있다.")
	@Test
	void admin_find_ticket_by_id_test() {
	    // Given
		Long ticketId = 1L;
		Ticket ticket = Ticket.builder()
			.id(ticketId)
			.matchId(1L)
			.seatId(1L)
			.userId(1L)
			.ticketNo("20250405-1-1-111")
			.status(TicketStatus.PENDING)
			.build();

		AdminTicketIdRequestServiceDto requestDto = new AdminTicketIdRequestServiceDto(ticketId);

		when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
		when(mapper.toAdminTicketResponseServiceDto(ticket)).thenReturn(
			new AdminTicketResponseServiceDto(
				ticketId,
				ticket.getMatchId(),
				ticket.getSeatId(),
				ticket.getUserId(),
				ticket.getTicketNo(),
				ticket.getStatus().name(),
				ticket.getIsDeleted()
			)
		);

		// When
		AdminTicketResponseServiceDto response = adminTicketService.getTicketById(requestDto);

	    // Then
		Assertions.assertThat(response).isNotNull()
			.extracting(
				AdminTicketResponseServiceDto::id,
				AdminTicketResponseServiceDto::matchId,
				AdminTicketResponseServiceDto::seatId,
				AdminTicketResponseServiceDto::userId,
				AdminTicketResponseServiceDto::ticketNo,
				AdminTicketResponseServiceDto::status,
				AdminTicketResponseServiceDto::isDeleted)
			.containsExactly(
				ticketId,
				ticket.getMatchId(),
				ticket.getSeatId(),
				ticket.getUserId(),
				ticket.getTicketNo(),
				ticket.getStatus().name(),
				ticket.getIsDeleted()
			);
	}

}