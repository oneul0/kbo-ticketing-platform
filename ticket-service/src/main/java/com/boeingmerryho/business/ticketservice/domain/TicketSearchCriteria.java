package com.boeingmerryho.business.ticketservice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TicketSearchCriteria {
	private final Long id;
	private final Long matchId;
	private final Long seatId;
	private final Long userId;
	private final String ticketNo;
	private final String status;
	private final Boolean isDeleted;
}
