package com.boeingmerryho.business.seatservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_seat_reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatReservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable = false
    )
    private Long seatId;

    @Column(
        nullable = false
    )
    private Long userId;

    @Column(
        nullable = false
    )
    private LocalDate reservationDate;

    @Column(
        nullable = false
    )
    private Boolean isReserved;
}