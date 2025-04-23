package com.boeingmerryho.business.ticketservice.infrastructure.service.feign.dto.request;

import java.util.Date;

public record IssuedTicketDto(
	Long ticketId,
	Long userId,
	Date matchDate
) {
}
