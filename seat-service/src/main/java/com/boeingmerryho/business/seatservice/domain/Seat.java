package com.boeingmerryho.business.seatservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
}