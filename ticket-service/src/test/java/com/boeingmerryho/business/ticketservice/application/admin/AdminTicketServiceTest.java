package com.boeingmerryho.business.ticketservice.application.admin;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.ticketservice.application.admin.dto.mapper.AdminTicketApplicationMapper;
import com.boeingmerryho.business.ticketservice.application.admin.dto.request.AdminTicketDeleteRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.admin.dto.request.AdminTicketIdRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.admin.dto.request.AdminTicketSearchRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.admin.dto.request.AdminTicketStatusUpdateRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.admin.dto.response.AdminTicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.TicketSearchCriteria;
import com.boeingmerryho.business.ticketservice.domain.TicketStatus;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketRepository;
import com.boeingmerryho.business.ticketservice.exception.ErrorCode;
import com.boeingmerryho.business.ticketservice.exception.TicketException;
import com.boeingmerryho.business.ticketservice.utils.PageableUtils;

@ExtendWith(MockitoExtension.class)
class AdminTicketServiceTest {

	@InjectMocks
	private AdminTicketService adminTicketService;

	@Mock
	private TicketRepository ticketRepository;

	@Mock
	private AdminTicketApplicationMapper mapper;

	private final Long ticketId = 1L;

	private Ticket setUpTicket() {
		return Ticket.builder()
			.id(ticketId)
			.matchId(1L)
			.seatId(1L)
			.userId(1L)
			.ticketNo("20250405-1-1-111")
			.status(TicketStatus.PENDING)
			.build();
	}

	private AdminTicketResponseServiceDto setUpResponseDto(Ticket ticket, TicketStatus status) {
		return new AdminTicketResponseServiceDto(
			ticket.getId(),
			ticket.getMatchId(),
			ticket.getSeatId(),
			ticket.getUserId(),
			ticket.getTicketNo(),
			status.name(),
			ticket.getIsDeleted()
		);
	}

	@DisplayName("관리자는 티켓 ID를 통해 티켓을 조회할 수 있다.")
	@Test
	void admin_find_ticket_by_id_test() {
		// Given
		Ticket ticket = setUpTicket();
		AdminTicketIdRequestServiceDto requestDto = new AdminTicketIdRequestServiceDto(ticketId);
		AdminTicketResponseServiceDto responseDto = setUpResponseDto(ticket, ticket.getStatus());

		when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
		when(mapper.toAdminTicketResponseServiceDto(ticket)).thenReturn(responseDto);

		// When
		AdminTicketResponseServiceDto response = adminTicketService.getTicketById(requestDto);

		// Then
		assertThat(response).isNotNull()
			.extracting(
				AdminTicketResponseServiceDto::id,
				AdminTicketResponseServiceDto::matchId,
				AdminTicketResponseServiceDto::seatId,
				AdminTicketResponseServiceDto::userId,
				AdminTicketResponseServiceDto::ticketNo,
				AdminTicketResponseServiceDto::status,
				AdminTicketResponseServiceDto::isDeleted
			)
			.containsExactly(
				ticket.getId(),
				ticket.getMatchId(),
				ticket.getSeatId(),
				ticket.getUserId(),
				ticket.getTicketNo(),
				ticket.getStatus().name(),
				ticket.getIsDeleted()
			);
	}

	@DisplayName("관리자는 조건을 통해 티켓을 조회할 수 있다.")
	@Test
	void admin_search_ticket_test() {
		// Given
		Ticket ticket = setUpTicket();
		Pageable pageable = PageableUtils.customPageable(0, 10, "createdAt", "desc");
		AdminTicketSearchRequestServiceDto requestDto = new AdminTicketSearchRequestServiceDto(
			null, 1L, null, null, null, null, null
		);
		AdminTicketResponseServiceDto responseDto = setUpResponseDto(ticket, ticket.getStatus());
		Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket), pageable, 1);

		when(ticketRepository.findByCriteria(any(TicketSearchCriteria.class), eq(pageable)))
			.thenReturn(ticketPage);
		when(mapper.toAdminTicketResponseServiceDto(ticket)).thenReturn(responseDto);

		// When
		Page<AdminTicketResponseServiceDto> result = adminTicketService.searchTickets(requestDto, pageable);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0)).isEqualTo(responseDto);

		verify(ticketRepository).findByCriteria(any(TicketSearchCriteria.class), eq(pageable));
		verify(mapper).toAdminTicketResponseServiceDto(ticket);
	}

	@DisplayName("관리자는 티켓의 상태를 변경할 수 있다.")
	@Test
	void admin_update_ticket_status_test() {
		// Given
		Ticket ticket = setUpTicket();
		AdminTicketStatusUpdateRequestServiceDto requestDto = new AdminTicketStatusUpdateRequestServiceDto(
			TicketStatus.CANCELLED.name()
		);
		AdminTicketResponseServiceDto responseDto = setUpResponseDto(ticket, TicketStatus.CANCELLED);

		when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
		when(mapper.toAdminTicketResponseServiceDto(ticket)).thenReturn(responseDto);

		// When
		AdminTicketResponseServiceDto result = adminTicketService.updateTicketStatus(ticketId, requestDto);

		// Then
		assertThat(result).isNotNull()
			.extracting(AdminTicketResponseServiceDto::status)
			.isEqualTo(TicketStatus.CANCELLED.name());
	}

	@DisplayName("올바르지 않은 티켓 상태로 업데이트 시도 시 예외가 발생한다.")
	@Test
	void invalid_status_update_exception_test() {
		// Given
		Ticket ticket = setUpTicket();
		AdminTicketStatusUpdateRequestServiceDto requestDto =
			new AdminTicketStatusUpdateRequestServiceDto("INVALID_STATUS");

		when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

		// When & Then
		assertThatThrownBy(() -> adminTicketService.updateTicketStatus(ticketId, requestDto))
			.isInstanceOfSatisfying(
				TicketException.class,
				ex -> assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_TICKET_STATUS)
			);
	}

	@DisplayName("관리자는 티켓을 soft delete 할 수 있다.")
	@Test
	void admin_ticket_soft_delete_test() {
	    // Given
	    Ticket ticket = setUpTicket();
		AdminTicketDeleteRequestServiceDto requestDto = new AdminTicketDeleteRequestServiceDto(ticketId);

	    when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

	    // When
		adminTicketService.softDeleteTicketById(requestDto);

	    // Then
		assertThat(ticket).isNotNull()
			.extracting(
				Ticket::getIsDeleted,
				Ticket::getStatus
			)
			.containsExactly(
				true,
				TicketStatus.CANCELLED
			);
	}
}
