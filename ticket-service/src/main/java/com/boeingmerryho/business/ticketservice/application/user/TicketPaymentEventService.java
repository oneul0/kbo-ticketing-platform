package com.boeingmerryho.business.ticketservice.application.user;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.ticketservice.infrastructure.service.feign.QueueClient;
import com.boeingmerryho.business.ticketservice.infrastructure.service.feign.dto.request.IssuedTicketDto;
import com.boeingmerryho.business.ticketservice.domain.TicketPaymentResult;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketPaymentRepository;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketRepository;
import com.boeingmerryho.business.ticketservice.domain.service.TicketStatusUpdateService;
import com.boeingmerryho.business.ticketservice.exception.ErrorCode;
import com.boeingmerryho.business.ticketservice.exception.TicketException;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.SeatKafkaProducer;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.request.SeatListDto;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.PaymentListenerDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketPaymentEventService {

	private static final String SEAT_SUCCESS_TOPIC = "seat-succeed";
	private static final String SEAT_FAIL_TOPIC = "seat-failed";

	private final QueueClient queueClient;
	private final TicketRepository ticketRepository;
	private final SeatKafkaProducer seatKafkaProducer;
	private final TicketPaymentRepository ticketPaymentRepository;
	private final TicketStatusUpdateService ticketStatusUpdateService;

	@Transactional
	public void handlePaymentEvent(PaymentListenerDto requestDto) {
		validateTicketIsNotEmpty(requestDto.tickets());

		TicketPaymentResult ticketPaymentResult = ticketStatusUpdateService
			.updateStatusByPaymentResult(requestDto.getPaymentStatus(), requestDto.tickets());

		// Redis 에 저장된 결제정보 삭제
		ticketPaymentRepository.deletePaymentInfo(ticketPaymentResult.userId());

		seatKafkaProducer.send(requestDto.isSuccess() ? SEAT_SUCCESS_TOPIC : SEAT_FAIL_TOPIC,
			new SeatListDto(requestDto.getMatchDate(), ticketPaymentResult.seatIds()));

		if (requestDto.isSuccess()) {
			queueClient.sendIssuedTicket(new IssuedTicketDto(
				getTicketIdByTicketNo(requestDto.tickets().get(0)),
				ticketPaymentResult.userId(),
				toDateFormat(requestDto.tickets().get(0))
			));
		}
	}

	private static void validateTicketIsNotEmpty(List<String> tickets) {
		if (tickets.isEmpty()) {
			throw new TicketException(ErrorCode.TICKET_LIST_EMPTY);
		}
	}

	private Long getTicketIdByTicketNo(String ticketNo) {
		return ticketRepository.findByTicketNo(ticketNo)
			.orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND))
			.getId();
	}

	private Date toDateFormat(String ticketNo) {
		return Date.from(
			LocalDate.parse(ticketNo.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"))
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant());
	}
}
