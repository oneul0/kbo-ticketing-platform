package com.boeingmerryho.business.userservice.infrastructure;

import static com.boeingmerryho.business.userservice.domain.QUser.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.boeingmerryho.business.userservice.domain.User;
import com.boeingmerryho.business.userservice.domain.UserRoleType;
import com.boeingmerryho.business.userservice.domain.UserSearchCriteria;
import com.boeingmerryho.business.userservice.domain.repository.CustomUserRepository;
import com.boeingmerryho.business.userservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository implements CustomUserRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<User> findDynamicQuery(UserSearchCriteria criteria, Pageable pageable) {
		BooleanBuilder conditions = new BooleanBuilder();
		conditions
			.and(eqId(criteria.getId()))
			.and(eqUsername(criteria.getUsername()))
			.and(eqNickname(criteria.getNickname()))
			.and(eqEmail(criteria.getEmail()))
			.and(eqRole(criteria.getRole()))
			.and(eqIsDeleted(criteria.getIsDeleted()));

		JPAQuery<User> query = queryFactory
			.selectFrom(user)
			.where(conditions)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), User.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		Long total = queryFactory
			.select(user.id.count())
			.from(user)
			.where(conditions)
			.fetchOne();
		assert total != null;

		return new PageImpl<>(query.fetch(), pageable, total);
	}

	@Override
	public Optional<User> findActiveUserById(Long id) {
		return Optional.ofNullable(queryFactory
			.selectFrom(user)
			.where(
				eqId(id),
				eqIsDeleted(Boolean.FALSE)
			)
			.fetchOne()
		);
	}

	@Override
	public Optional<List<User>> findUsersByRole(UserRoleType role, Boolean isDeleted) {
		List<User> users = queryFactory
			.selectFrom(user)
			.where(
				eqRole(role),
				eqIsDeleted(isDeleted)
			)
			.fetch();

		return Optional.ofNullable(users.isEmpty() ? null : users);
	}

	private BooleanExpression eqId(Long id) {
		if (id == null) {
			return null;
		}
		return user.id.eq(id);
	}

	private BooleanExpression eqUsername(String username) {
		if (!StringUtils.hasText(username)) {
			return null;
		}
		return user.username.contains(username);
	}

	private BooleanExpression eqNickname(String nickname) {
		if (!StringUtils.hasText(nickname)) {
			return null;
		}
		return user.nickname.contains(nickname);
	}

	private BooleanExpression eqEmail(String email) {
		if (!StringUtils.hasText(email)) {
			return null;
		}
		return user.email.contains(email);
	}

	private BooleanExpression eqRole(UserRoleType role) {
		if (role == null) {
			return null;
		}
		return user.role.eq(role);
	}

	private BooleanExpression eqIsDeleted(Boolean isDeleted) {
		if (isDeleted == null) {
			return null;
		}
		return user.isDeleted.eq(isDeleted);
	}
}
