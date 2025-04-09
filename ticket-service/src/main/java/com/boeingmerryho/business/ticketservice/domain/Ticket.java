package com.boeingmerryho.business.ticketservice.domain;

import io.github.boeingmerryho.commonlibrary.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "p_ticket")
public class Ticket extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long matchId;

	@Column(nullable = false)
	private Long seatId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private String ticketNo;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private TicketStatus status;

}
