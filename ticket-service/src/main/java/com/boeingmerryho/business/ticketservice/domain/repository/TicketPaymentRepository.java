package com.boeingmerryho.business.ticketservice.domain.repository;

import java.util.List;
import java.util.Map;

import com.boeingmerryho.business.ticketservice.domain.Ticket;

public interface TicketPaymentRepository {
	void savePaymentInfo(Long userId, Map<String, Object> paymentInfo);

	Map<Object, Object> getPaymentInfo(Long userId);

	void deletePaymentInfo(Long userId);

	void saveFailedPayment(List<Ticket> tickets);
}
