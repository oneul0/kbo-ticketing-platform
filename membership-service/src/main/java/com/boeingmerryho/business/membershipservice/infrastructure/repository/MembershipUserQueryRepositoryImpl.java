package com.boeingmerryho.business.membershipservice.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.membershipservice.application.dto.query.MembershipUserSearchCondition;
import com.boeingmerryho.business.membershipservice.application.dto.response.MembershipUserSearchAdminResponseServiceDto;
import com.boeingmerryho.business.membershipservice.domain.entity.MembershipUser;
import com.boeingmerryho.business.membershipservice.domain.entity.QMembership;
import com.boeingmerryho.business.membershipservice.domain.entity.QMembershipUser;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipUserQueryRepository;
import com.boeingmerryho.business.membershipservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MembershipUserQueryRepositoryImpl implements MembershipUserQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<MembershipUserSearchAdminResponseServiceDto> searchMembershipUser(
		MembershipUserSearchCondition condition, Pageable pageable) {

		QMembershipUser mu = QMembershipUser.membershipUser;
		QMembership m = QMembership.membership;

		BooleanBuilder where = buildCondition(mu, m, condition);

		List<MembershipUserSearchAdminResponseServiceDto> content = queryFactory
			.select(Projections.constructor(
				MembershipUserSearchAdminResponseServiceDto.class,
				mu.id,
				mu.userId,
				mu.membership.id,
				m.season,
				m.name.stringValue(),
				m.discount,
				mu.isActive,
				mu.isDeleted
			))
			.from(mu)
			.join(mu.membership, m)
			.where(where)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), MembershipUser.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(mu.count())
			.from(mu)
			.join(mu.membership, m)
			.where(where)
			.fetchOne();

		return new PageImpl<>(
			content,
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
			total != null ? total : 0L);
	}

	private BooleanBuilder buildCondition(QMembershipUser mu, QMembership m, MembershipUserSearchCondition condition) {
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(eqUserId(mu, condition.userId()));
		builder.and(eqMembershipId(mu, condition.membershipId()));
		builder.and(eqSeason(m, condition.season()));
		builder.and(likeName(m, condition.name()));
		builder.and(greaterThanOrEqualDiscount(m, condition.minDiscount()));
		builder.and(lessThanOrEqualDiscount(m, condition.maxDiscount()));
		builder.and(eqIsDeleted(mu, condition.isDeleted()));
		return builder;
	}

	private BooleanExpression eqUserId(QMembershipUser mu, Long userId) {
		return userId != null ? mu.userId.eq(userId) : null;
	}

	private BooleanExpression eqMembershipId(QMembershipUser mu, Long membershipId) {
		return membershipId != null ? mu.membership.id.eq(membershipId) : null;
	}

	private BooleanExpression eqSeason(QMembership m, Integer season) {
		return season != null ? m.season.eq(season) : null;
	}

	private BooleanExpression likeName(QMembership m, String name) {
		return name != null ? m.name.stringValue().containsIgnoreCase(name) : null;
	}

	private BooleanExpression greaterThanOrEqualDiscount(QMembership m, Double minDiscount) {
		return minDiscount != null ? m.discount.goe(minDiscount) : null;
	}

	private BooleanExpression lessThanOrEqualDiscount(QMembership m, Double maxDiscount) {
		return maxDiscount != null ? m.discount.loe(maxDiscount) : null;
	}

	private BooleanExpression eqIsDeleted(QMembershipUser mu, Boolean isDeleted) {
		return isDeleted != null ? mu.isDeleted.eq(isDeleted) : null;
	}
}

