package com.boeingmerryho.business.seatservice.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.boeingmerryho.business.seatservice.application.dto.request.SeatServiceUpdateDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_seat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(
		nullable = false,
		length = 30
	)
	private String name;

	@Column(
		nullable = false
	)
	private Integer seatBlock;

	@Column(
		nullable = false
	)
	private Integer seatColumn;

	@Column(
		nullable = false
	)
	private Integer seatRow;

	@Column(
		nullable = false,
		length = 10
	)
	private String seatNo;

	@Column(
		nullable = false
	)
	private Integer price;

	@Column(
		nullable = false
	)
	private Boolean isActive;

	@OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<SeatReservation> seatReservations = new ArrayList<>();

	public void active() {
		isActive = true;
	}

	public void inActive() {
		isActive = false;
	}

	public void update(SeatServiceUpdateDto update) {
		if (update.name() != null) {
			this.name = update.name();
		}
		if (update.price() != null) {
			this.price = update.price();
		}
	}
}