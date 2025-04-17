package com.boeingmerryho.business.ticketservice.application.user.dto.response;

import java.util.List;

public record TicketPaymentResponseServiceDto(
	Long paymentId,
	List<TicketInfo> ticketInfos
) {
}
