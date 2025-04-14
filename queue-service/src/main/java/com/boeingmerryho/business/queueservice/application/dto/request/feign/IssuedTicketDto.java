package com.boeingmerryho.business.queueservice.application.dto.request.feign;

import java.util.Date;

public record IssuedTicketDto(Long ticketId, Long userId, Date matchDate) {
}
