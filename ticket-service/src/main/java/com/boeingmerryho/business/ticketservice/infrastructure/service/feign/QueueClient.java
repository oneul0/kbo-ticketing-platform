package com.boeingmerryho.business.ticketservice.infrastructure.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.boeingmerryho.business.ticketservice.infrastructure.service.feign.dto.request.IssuedTicketDto;

@FeignClient(name = "queue-service")
public interface QueueClient {

	@PostMapping("/queue-service/ticket")
	void sendIssuedTicket(@RequestBody IssuedTicketDto dto);
}
