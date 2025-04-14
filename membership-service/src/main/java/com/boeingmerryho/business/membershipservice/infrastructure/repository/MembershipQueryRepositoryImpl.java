package com.boeingmerryho.business.membershipservice.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.membershipservice.application.dto.query.MembershipSearchCondition;
import com.boeingmerryho.business.membershipservice.domain.entity.Membership;
import com.boeingmerryho.business.membershipservice.domain.entity.QMembership;
import com.boeingmerryho.business.membershipservice.domain.repository.MembershipQueryRepository;
import com.boeingmerryho.business.membershipservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MembershipQueryRepositoryImpl implements MembershipQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Membership> search(MembershipSearchCondition condition, Pageable pageable) {
		QMembership membership = QMembership.membership;

		BooleanBuilder where = buildCondition(membership, condition);

		List<Membership> content = queryFactory
			.selectFrom(membership)
			.where(where)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Membership.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(membership.count())
			.from(membership)
			.where(where)
			.fetchOne();

		return new PageImpl<>(
			content,
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
			total != null ? total : 0L);
	}

	private BooleanBuilder buildCondition(QMembership membership, MembershipSearchCondition condition) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(eqSeason(membership, condition.season()));
		builder.and(likeName(membership, condition.name()));
		builder.and(greaterThanOrEqualDiscount(membership, condition.minDiscount()));
		builder.and(lessThanOrEqualDiscount(membership, condition.maxDiscount()));
		builder.and(greaterThanOrEqualAvailableQuantity(membership, condition.minAvailableQuantity()));
		builder.and(lessThanOrEqualAvailableQuantity(membership, condition.maxAvailableQuantity()));
		builder.and(greaterThanOrEqualPrice(membership, condition.minPrice()));
		builder.and(lessThanOrEqualPrice(membership, condition.maxPrice()));
		builder.and(eqIsDeleted(membership, condition.isDeleted()));
		return builder;
	}

	private BooleanBuilder eqSeason(QMembership membership, Integer season) {
		return season != null ? new BooleanBuilder(membership.season.eq(season)) : new BooleanBuilder();
	}

	private BooleanBuilder likeName(QMembership membership, String name) {
		return name != null && !name.isBlank()
			? new BooleanBuilder(membership.name.stringValue().containsIgnoreCase(name))
			: new BooleanBuilder();
	}

	private BooleanBuilder greaterThanOrEqualDiscount(QMembership membership, Double minDiscount) {
		return minDiscount != null
			? new BooleanBuilder(membership.discount.goe(minDiscount))
			: new BooleanBuilder();
	}

	private BooleanBuilder lessThanOrEqualDiscount(QMembership membership, Double maxDiscount) {
		return maxDiscount != null
			? new BooleanBuilder(membership.discount.loe(maxDiscount))
			: new BooleanBuilder();
	}

	private BooleanBuilder eqIsDeleted(QMembership membership, Boolean isDeleted) {
		return isDeleted != null
			? new BooleanBuilder(membership.isDeleted.eq(isDeleted))
			: new BooleanBuilder();
	}

	private BooleanBuilder greaterThanOrEqualAvailableQuantity(QMembership membership, Integer minQuantity) {
		return minQuantity != null
			? new BooleanBuilder(membership.availableQuantity.goe(minQuantity))
			: new BooleanBuilder();
	}

	private BooleanBuilder lessThanOrEqualAvailableQuantity(QMembership membership, Integer maxQuantity) {
		return maxQuantity != null
			? new BooleanBuilder(membership.availableQuantity.loe(maxQuantity))
			: new BooleanBuilder();
	}

	private BooleanBuilder greaterThanOrEqualPrice(QMembership membership, Integer minPrice) {
		return minPrice != null
			? new BooleanBuilder(membership.price.goe(minPrice))
			: new BooleanBuilder();
	}

	private BooleanBuilder lessThanOrEqualPrice(QMembership membership, Integer maxPrice) {
		return maxPrice != null
			? new BooleanBuilder(membership.price.loe(maxPrice))
			: new BooleanBuilder();
	}

}
