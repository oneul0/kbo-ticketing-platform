package com.boeingmerryho.business.ticketservice.application.user;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.ticketservice.application.user.dto.mapper.TicketApplicationMapper;
import com.boeingmerryho.business.ticketservice.application.user.dto.request.TicketByIdRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.request.TicketPaymentRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.request.TicketSearchRequestServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketPaymentResponseServiceDto;
import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketResponseServiceDto;
import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.TicketSearchCriteria;
import com.boeingmerryho.business.ticketservice.domain.TicketStatus;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketRepository;
import com.boeingmerryho.business.ticketservice.domain.service.CreateTicketService;
import com.boeingmerryho.business.ticketservice.domain.service.RemoveTicketPaymentInfoService;
import com.boeingmerryho.business.ticketservice.exception.ErrorCode;
import com.boeingmerryho.business.ticketservice.exception.TicketException;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.SeatKafkaProducer;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.request.SeatListDto;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.PaymentListenerDto;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatInfo;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatListenerDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketService {

	private final TicketRepository ticketRepository;
	private final SeatKafkaProducer seatKafkaProducer;
	private final CreateTicketService createTicketService;
	private final TicketPaymentService ticketPaymentService;
	private final RemoveTicketPaymentInfoService removeTicketPaymentInfoService;
	private final TicketApplicationMapper mapper;

	@Transactional(readOnly = true)
	public Page<TicketResponseServiceDto> getMyTickets(TicketSearchRequestServiceDto requestDto, Pageable pageable) {
		Page<Ticket> tickets = ticketRepository.findByCriteria(createTicketSearchCriteria(requestDto), pageable);

		return tickets.map(mapper::toTicketResponseDto);
	}

	@Transactional(readOnly = true)
	public TicketResponseServiceDto getTicketById(TicketByIdRequestServiceDto requestDto) {
		Ticket ticket = ticketRepository.findActiveTicketById(requestDto.id())
			.orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND));

		return mapper.toTicketResponseDto(ticket);
	}

	@Transactional
	public void handleSeatEvent(SeatListenerDto requestDto) {
		List<Ticket> tickets = createTicketService.createTickets(requestDto);
		// TODO : Redis 에 저장하기
		for (Ticket ticket : tickets) {
			ticketRepository.save(ticket);
		}

		List<SeatInfo> seats = requestDto.seatsInfo();
		ticketPaymentService.createPaymentForTickets(tickets, seats);
	}

	@Transactional(readOnly = true)
	public TicketPaymentResponseServiceDto getTicketPaymentInfo(TicketPaymentRequestServiceDto requestDto) {
		return ticketPaymentService.getTicketPaymentInfo(requestDto.userId());
	}

	@Transactional
	public void handlePaymentEvent(PaymentListenerDto requestDto) {
		boolean isSuccess = requestDto.event().equals("success");

		TicketStatus status = isSuccess ? TicketStatus.CONFIRMED : TicketStatus.CANCELLED;
		String topic = isSuccess ? "seat-succeed" : "seat-failed";

		Long userId = 0L;
		List<String> seatIds = new ArrayList<>();

		for (String ticketNo : requestDto.tickets()) {
			Ticket ticket = ticketRepository.findByTicketNo(ticketNo)
				.orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND));

			ticket.updateStatus(status.name());

			userId = ticket.getUserId();
			seatIds.add(ticket.getSeatId().toString());
		}

		// Redis 에 저장된 결제정보 삭제
		removeTicketPaymentInfoService.removeTicketPaymentInfo(userId);

		String matchDate = createMatchDate(requestDto.tickets().get(0));
		seatKafkaProducer.send(topic, new SeatListDto(matchDate, seatIds));
	}

	private String createMatchDate(String ticketNo) {
		return LocalDate.parse(ticketNo.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"))
			.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	private TicketSearchCriteria createTicketSearchCriteria(TicketSearchRequestServiceDto requestDto) {
		return TicketSearchCriteria.builder()
			.matchId(requestDto.matchId())
			.seatId(requestDto.seatId())
			.userId(requestDto.userId())
			.ticketNo(requestDto.ticketNo())
			.status(requestDto.status())
			.isDeleted(Boolean.FALSE)
			.build();
	}
}

