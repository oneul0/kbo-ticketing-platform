package com.boeingmerryho.business.ticketservice.infrastructure.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.ticketservice.application.feign.PaymentClient;
import com.boeingmerryho.business.ticketservice.application.feign.dto.request.PaymentCreationRequestDto;
import com.boeingmerryho.business.ticketservice.application.feign.dto.response.PaymentCreationResponseDto;
import com.boeingmerryho.business.ticketservice.application.user.TicketPaymentService;
import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatInfo;

@Service
public class TicketPaymentServiceImpl implements TicketPaymentService {

	private final PaymentClient paymentClient;
	private final RedisTemplate<String, Object> redisTemplate;

	public TicketPaymentServiceImpl(
		PaymentClient paymentClient,
		@Qualifier("ticketRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
		this.paymentClient = paymentClient;
		this.redisTemplate = redisTemplate;
	}

	@Override
	@Transactional
	public void createPaymentForTickets(List<Ticket> tickets, List<SeatInfo> seats) {
		String userId = tickets.get(0).getUserId().toString();

		// TODO : 에러 처리
		PaymentCreationResponseDto responseDto = paymentClient.createPayment(createRequestDto(tickets, seats));

		String redisKey = "ticket:payment:" + userId;
		Map<String, Object> paymentInfoMap = new HashMap<>();
		paymentInfoMap.put("paymentId", responseDto.paymentId());

		List<Map<String, Object>> ticketInfoList = new ArrayList<>();
		for (Ticket ticket : tickets) {
			Map<String, Object> ticketInfo = new HashMap<>();
			ticketInfo.put("ticketNo", ticket.getTicketNo());
			ticketInfo.put("price", ticket.getPrice());
			ticketInfoList.add(ticketInfo);
		}
		paymentInfoMap.put("ticketInfos", ticketInfoList);

		redisTemplate.opsForHash().putAll(redisKey, paymentInfoMap); // TODO : TTL 설정하기(무통장입금일 경우 고려하기)
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
}
