package com.boeingmerryho.business.ticketservice.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.ticketservice.domain.QTicket;
import com.boeingmerryho.business.ticketservice.domain.Ticket;
import com.boeingmerryho.business.ticketservice.domain.TicketSearchCriteria;
import com.boeingmerryho.business.ticketservice.domain.TicketStatus;
import com.boeingmerryho.business.ticketservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TicketQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<Ticket> findByCriteria(TicketSearchCriteria criteria, Pageable pageable) {
		BooleanBuilder condition = new BooleanBuilder();
		condition
			.and(eqId(criteria.getId()))
			.and(eqMatchId(criteria.getMatchId()))
			.and(eqSeatId(criteria.getSeatId()))
			.and(eqUserId(criteria.getUserId()))
			.and(containsTicketNo(criteria.getTicketNo()))
			.and(eqStatus(criteria.getStatus()))
			.and(eqIsDeleted(criteria.getIsDeleted()));

		JPAQuery<Ticket> query = queryFactory
			.selectFrom(QTicket.ticket)
			.where(condition)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Ticket.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		Long total = queryFactory
			.select(QTicket.ticket.id.count())
			.from(QTicket.ticket)
			.where(condition)
			.fetchOne();

		return new PageImpl<>(query.fetch(), pageable, total == null ? 0 : total);
	}

	private BooleanExpression eqId(Long id) {
		if (id == null) {
			return null;
		}
		return QTicket.ticket.id.eq(id);
	}

	private BooleanExpression eqMatchId(Long matchId) {
		if (matchId == null) {
			return null;
		}
		return QTicket.ticket.matchId.eq(matchId);
	}

	private BooleanExpression eqSeatId(Long seatId) {
		if (seatId == null) {
			return null;
		}
		return QTicket.ticket.seatId.eq(seatId);
	}

	private BooleanExpression eqUserId(Long userId) {
		if (userId == null) {
			return null;
		}
		return QTicket.ticket.userId.eq(userId);
	}

	private BooleanExpression containsTicketNo(String ticketNo) {
		if (ticketNo == null) {
			return null;
		}
		return QTicket.ticket.ticketNo.contains(ticketNo);
	}

	private BooleanExpression eqStatus(String status) {
		if (status == null) {
			return null;
		}
		return QTicket.ticket.status.eq(TicketStatus.valueOf(status));
	}

	private BooleanExpression eqIsDeleted(Boolean isDeleted) {
		if (isDeleted == null) {
			return null;
		}
		return QTicket.ticket.isDeleted.eq(isDeleted);
	}
}
