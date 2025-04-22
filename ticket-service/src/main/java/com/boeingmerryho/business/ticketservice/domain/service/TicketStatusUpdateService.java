package com.boeingmerryho.business.ticketservice.domain.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.TicketPaymentResult;
import com.boeingmerryho.business.ticketservice.domain.TicketStatus;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketRepository;
import com.boeingmerryho.business.ticketservice.exception.ErrorCode;
import com.boeingmerryho.business.ticketservice.exception.TicketException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketStatusUpdateService {

	private final TicketRepository ticketRepository;

	public TicketPaymentResult updateStatusByPaymentResult(TicketStatus status, List<String> tickets) {
		Long userId = null;
		List<String> seatIds = new ArrayList<>();

		for (String ticketNo : tickets) {
			Ticket ticket = ticketRepository.findByTicketNo(ticketNo)
				.orElseThrow(() -> new TicketException(ErrorCode.TICKET_NOT_FOUND));

			ticket.updateStatus(status.name());

			// userId가 처음 설정되는 경우
			if (userId == null) {
				userId = ticket.getUserId();
			}
			// 이후 티켓의 userId가 일치하지 않으면 예외 발생
			else if (!userId.equals(ticket.getUserId())) {
				throw new TicketException(ErrorCode.INVALID_USER_ID);
			}
			seatIds.add(ticket.getSeatId().toString());
		}

		return new TicketPaymentResult(userId, seatIds);
	}
}
