package com.boeingmerryho.business.ticketservice.domain.repository;

import java.util.Optional;

import com.boeingmerryho.business.ticketservice.domain.Ticket;

public interface TicketRepository {
	Optional<Ticket> findById(Long id);
}
