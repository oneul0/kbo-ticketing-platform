package com.boeingmerryho.business.seatservice.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boeingmerryho.business.seatservice.domain.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
	List<Seat> findAllByIsActiveIsTrue();
}