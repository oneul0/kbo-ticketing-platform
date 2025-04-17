package com.boeingmerryho.business.ticketservice.presentation.user.dto.response;

import java.util.List;

public record TicketPaymentResponseDto(
	Long paymentId,
	List<TicketInfo> ticketInfos
) {
}
