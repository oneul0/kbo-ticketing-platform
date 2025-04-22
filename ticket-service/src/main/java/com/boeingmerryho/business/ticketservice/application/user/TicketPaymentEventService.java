package com.boeingmerryho.business.ticketservice.application.user;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.ticketservice.application.feign.QueueClient;
import com.boeingmerryho.business.ticketservice.application.feign.dto.request.IssuedTicketDto;
import com.boeingmerryho.business.ticketservice.domain.TicketPaymentResult;
import com.boeingmerryho.business.ticketservice.domain.TicketStatus;
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

	private static final String SUCCESS_EVENT = "success";
	private static final String SEAT_SUCCESS_TOPIC = "seat-succeed";
	private static final String SEAT_FAIL_TOPIC = "seat-failed";

	private final QueueClient queueClient;
	private final TicketRepository ticketRepository;
	private final SeatKafkaProducer seatKafkaProducer;
	private final TicketPaymentRepository ticketPaymentRepository;
	private final TicketStatusUpdateService ticketStatusUpdateService;

	@Transactional
	public void handlePaymentEvent(PaymentListenerDto requestDto) {
		if (requestDto.tickets().isEmpty()) {
			throw new TicketException(ErrorCode.TICKET_LIST_EMPTY);
		}

		boolean isSuccess = requestDto.event().equals(SUCCESS_EVENT);
		TicketStatus status = isSuccess ? TicketStatus.CONFIRMED : TicketStatus.CANCELLED;
		String topic = isSuccess ? SEAT_SUCCESS_TOPIC : SEAT_FAIL_TOPIC;

		TicketPaymentResult ticketPaymentResult = ticketStatusUpdateService
			.updateStatusByPaymentResult(status, requestDto.tickets());

		// Redis 에 저장된 결제정보 삭제
		ticketPaymentRepository.deletePaymentInfo(ticketPaymentResult.userId());

		String matchDate = createMatchDate(requestDto.tickets().get(0));
		seatKafkaProducer.send(topic, new SeatListDto(matchDate, ticketPaymentResult.seatIds()));

		if (isSuccess) {
			queueClient.sendIssuedTicket(new IssuedTicketDto(
				getTicketIdByTicketNo(requestDto.tickets().get(0)),
				ticketPaymentResult.userId(),
				toDateFormat(requestDto.tickets().get(0))
			));
		}
	}

	private Long getTicketIdByTicketNo(String ticketNo) {
		return ticketRepository.findByTicketNo(ticketNo)
			.orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND))
			.getId();
	}

	private String createMatchDate(String ticketNo) {
		return LocalDate.parse(ticketNo.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"))
			.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	private Date toDateFormat(String ticketNo) {
		return Date.from(
			LocalDate.parse(ticketNo.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd"))
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant());
	}
}
