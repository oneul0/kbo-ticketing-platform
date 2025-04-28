package com.boeingmerryho.business.ticketservice.infrastructure.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.ticketservice.infrastructure.service.feign.PaymentClient;
import com.boeingmerryho.business.ticketservice.infrastructure.service.feign.dto.request.PaymentCreationRequestDto;
import com.boeingmerryho.business.ticketservice.infrastructure.service.feign.dto.response.PaymentCreationResponseDto;
import com.boeingmerryho.business.ticketservice.application.user.TicketPaymentService;
import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketInfo;
import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketPaymentResponseServiceDto;
import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketPaymentRepository;
import com.boeingmerryho.business.ticketservice.exception.ErrorCode;
import com.boeingmerryho.business.ticketservice.exception.TicketException;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatInfo;

import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Counted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketPaymentServiceImpl implements TicketPaymentService {

	private final PaymentClient paymentClient;
	private final TicketPaymentRepository ticketPaymentRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Retry(name = "paymentRetry", fallbackMethod = "fallbackCreatePayment")
	public void createPaymentForTickets(List<Ticket> tickets, List<SeatInfo> seats) {
		Long userId = tickets.get(0).getUserId();
		PaymentCreationRequestDto requestDto = createRequestDto(tickets, seats);

		PaymentCreationResponseDto responseDto = paymentClient.createPayment(requestDto);

		Map<String, Object> paymentInfo = buildPaymentInfo(responseDto, tickets);
		ticketPaymentRepository.savePaymentInfo(userId, paymentInfo);
	}

	@Override
	public TicketPaymentResponseServiceDto getTicketPaymentInfo(Long userId) {
		Map<Object, Object> paymentInfoMap = ticketPaymentRepository.getPaymentInfo(userId);

		if (paymentInfoMap.isEmpty()) {
			throw new TicketException(ErrorCode.TICKET_PAYMENT_NOT_FOUND);
		}

		Object paymentIdObj = paymentInfoMap.get("paymentId");
		Long paymentId;
		if (paymentIdObj instanceof Number number) {
			paymentId = number.longValue();
		} else {
			throw new TicketException(ErrorCode.TICKET_PAYMENT_INVALID_FIELD);
		}

		List<Map<String, Object>> ticketInfoList = (List<Map<String, Object>>)paymentInfoMap.get("ticketInfos");
		List<TicketInfo> ticketInfos = ticketInfoList.stream()
			.map(map -> new TicketInfo(
				(String)map.get("ticketNo"),
				(Integer)map.get("price")
			))
			.toList();

		return new TicketPaymentResponseServiceDto(paymentId, ticketInfos);
	}

	private PaymentCreationRequestDto createRequestDto(List<Ticket> tickets, List<SeatInfo> seats) {
		return new PaymentCreationRequestDto(
			tickets.get(0).getUserId(),
			tickets.get(0).getPrice(),
			tickets.size(),
			"TICKET",
			LocalDateTime.parse(seats.get(0).expiredAt())
		);
	}

	private Map<String, Object> buildPaymentInfo(PaymentCreationResponseDto responseDto, List<Ticket> tickets) {
		Map<String, Object> paymentInfoMap = new HashMap<>();
		paymentInfoMap.put("paymentId", responseDto.paymentId());

		List<Map<String, Object>> ticketInfoList = tickets.stream().map(ticket -> {
			Map<String, Object> map = new HashMap<>();
			map.put("ticketNo", ticket.getTicketNo());
			map.put("price", ticket.getPrice());
			return map;
		}).toList();

		paymentInfoMap.put("ticketInfos", ticketInfoList);
		return paymentInfoMap;
	}

	@Counted(value = "ticket.payment.failure", description = "Number of payment failures")
	public void fallbackCreatePayment(List<Ticket> tickets, List<SeatInfo> seats, Throwable t) {
		log.error("결제 서비스 호출 실패 - fallback 수행");
		ticketPaymentRepository.saveFailedPayment(tickets);
	}
}
