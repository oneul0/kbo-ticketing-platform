package com.boeingmerryho.business.ticketservice.application.feign.dto.request;

import java.util.Date;

public record IssuedTicketDto(
	Long ticketId,
	Long userId,
	Date matchDate
) {
}
