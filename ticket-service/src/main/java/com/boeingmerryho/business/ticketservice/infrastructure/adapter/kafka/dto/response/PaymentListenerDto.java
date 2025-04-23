package com.boeingmerryho.business.ticketservice.infrastructure.adapter.kafka.dto.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.boeingmerryho.business.ticketservice.domain.PaymentStatus;

/**
 *
 * @param event
 * @param tickets [경기날짜]-[구장]-[경기ID]-[좌석블럭:좌석열:좌석행] e.g.) 20250417-1-9-1010101
 */
public record PaymentListenerDto(
	String event,
	List<String> tickets
) {
	public String getMatchDate() {
		List<LocalDate> dates = this.tickets.stream()
			.map(ticket -> LocalDate.parse(ticket.substring(0, 8), DateTimeFormatter.ofPattern("yyyyMMdd")))
			.distinct()
			.toList();

		if (dates.size() != 1) {
			throw new IllegalArgumentException("경기날짜가 다릅니다.");
		}

		return dates.get(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}

	public PaymentStatus getPaymentStatus() {
		return PaymentStatus.from(this.event);
	}

	public boolean isSuccess() {
		return this.getPaymentStatus() == PaymentStatus.SUCCESS;
	}
}
