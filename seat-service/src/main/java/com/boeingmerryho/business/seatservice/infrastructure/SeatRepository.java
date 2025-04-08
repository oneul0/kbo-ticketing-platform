package com.boeingmerryho.business.seatservice.infrastructure;

import com.boeingmerryho.business.seatservice.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}