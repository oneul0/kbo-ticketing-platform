package com.boeingmerryho.business.ticketservice.domain.repository;

import java.util.Map;

public interface TicketPaymentRepository {
	void savePaymentInfo(Long userId, Map<String, Object> paymentInfo);

	Map<Object, Object> getPaymentInfo(Long userId);

	void deletePaymentInfo(Long userId);
}
