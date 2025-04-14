package com.boeingmerryho.business.seatservice.application.dto.request;

import java.util.List;

public record ToTicketDto(
	ToTicketMatchDto matchInfo,
	List<ToTicketSeatDto> seatsInfo
) {
}