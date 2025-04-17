package com.boeingmerryho.business.ticketservice.application.user;

import java.util.List;

import com.boeingmerryho.business.ticketservice.application.user.dto.response.TicketPaymentResponseServiceDto;
import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response.SeatInfo;

public interface TicketPaymentService {

	void createPaymentForTickets(List<Ticket> tickets, List<SeatInfo> seats);

	TicketPaymentResponseServiceDto getTicketPaymentInfo(Long userId);
}
