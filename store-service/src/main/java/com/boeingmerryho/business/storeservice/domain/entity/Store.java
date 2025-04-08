package com.boeingmerryho.business.storeservice.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@Table(name = "p_store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        nullable = false
    )
    private Long stadiumId;

    @Column(
        nullable = false,
        length = 50
    )
    private String name;

    @Column(
        nullable = false
    )
    private LocalDateTime openAt;

    @Column(
        nullable = false
    )
    private LocalDateTime closedAt;

    @Column(
        nullable = false
    )
    private Boolean isClosed;
}
