package com.boeingmerryho.business.ticketservice.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepository {

	private final TicketJpaRepository ticketJpaRepository;
	private final TicketQueryRepository ticketQueryRepository;

	@Override
	public Optional<Ticket> findById(Long id) {
		return ticketJpaRepository.findById(id);
	}
}
