package com.boeingmerryho.business.storeservice.infrastructure.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.storeservice.application.dto.query.StoreSearchCondition;
import com.boeingmerryho.business.storeservice.domain.entity.QStore;
import com.boeingmerryho.business.storeservice.domain.entity.Store;
import com.boeingmerryho.business.storeservice.domain.repository.StoreQueryRepository;
import com.boeingmerryho.business.storeservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StoreQueryRepositoryImpl implements StoreQueryRepository {

	private final EntityManager entityManager;

	@Override
	public Page<Store> search(StoreSearchCondition condition, Pageable pageable) {
		JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
		QStore store = QStore.store;

		BooleanBuilder where = buildCondition(store, condition);

		List<Store> content = queryFactory
			.selectFrom(store)
			.where(where)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Store.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(store.count())
			.from(store)
			.where(where)
			.fetchOne();

		return new PageImpl<>(
			content,
			PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
			total != null ? total : 0L);
	}

	private BooleanBuilder buildCondition(QStore store, StoreSearchCondition condition) {
		BooleanBuilder builder = new BooleanBuilder();

		builder.and(equalStadiumId(store, condition.stadiumId()));
		builder.and(likeName(store, condition.name()));
		builder.and(equalIsClosed(store, condition.isClosed()));
		builder.and(equalIsDeleted(store, condition.isDeleted()));

		return builder;
	}

	private BooleanBuilder equalStadiumId(QStore store, Long stadiumId) {
		return stadiumId != null ? new BooleanBuilder(store.stadiumId.eq(stadiumId)) : new BooleanBuilder();
	}

	private BooleanBuilder likeName(QStore store, String name) {
		return (name != null && !name.isBlank())
			? new BooleanBuilder(store.name.containsIgnoreCase(name))
			: new BooleanBuilder();
	}

	private BooleanBuilder equalIsClosed(QStore store, Boolean isClosed) {
		return isClosed != null ? new BooleanBuilder(store.isClosed.eq(isClosed)) : new BooleanBuilder();
	}

	private BooleanBuilder equalIsDeleted(QStore store, Boolean isDeleted) {
		return isDeleted != null ? new BooleanBuilder(store.isDeleted.eq(isDeleted)) : new BooleanBuilder();
	}

}
