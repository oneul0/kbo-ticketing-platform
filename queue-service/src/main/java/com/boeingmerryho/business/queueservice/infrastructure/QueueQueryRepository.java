package com.boeingmerryho.business.queueservice.infrastructure;

import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.queueservice.domain.repository.CustomQueueRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueueQueryRepository implements CustomQueueRepository {

	private final JPAQueryFactory queryFactory;

}
