package com.boeingmerryho.business.ticketservice.infrastructure.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TicketQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;
}
