package com.boeingmerryho.business.ticketservice.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.TicketSearchCriteria;

public interface TicketRepository {
	void save(Ticket ticket);

	Optional<Ticket> findById(Long id);

	Page<Ticket> findByCriteria(TicketSearchCriteria criteria, Pageable pageable);

	Optional<Ticket> findActiveTicketById(Long id);
}
