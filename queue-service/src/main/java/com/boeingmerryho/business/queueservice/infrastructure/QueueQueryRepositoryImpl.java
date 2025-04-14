package com.boeingmerryho.business.queueservice.infrastructure;

import static com.boeingmerryho.business.queueservice.domain.entity.QQueue.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.queueservice.domain.entity.Queue;
import com.boeingmerryho.business.queueservice.domain.entity.QueueSearchCriteria;
import com.boeingmerryho.business.queueservice.domain.model.CancelReason;
import com.boeingmerryho.business.queueservice.domain.model.QueueStatus;
import com.boeingmerryho.business.queueservice.domain.repository.CustomQueueRepository;
import com.boeingmerryho.business.queueservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueueQueryRepositoryImpl implements CustomQueueRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Queue> findDynamicQuery(QueueSearchCriteria criteria, Pageable pageable) {
		BooleanBuilder conditions = new BooleanBuilder();
		conditions
			.and(eqId(criteria.getId()))
			.and(eqStoreId(criteria.getStoreId()))
			.and(eqUserId(criteria.getUserId()))
			.and(eqStatus(criteria.getStatus()))
			.and(eqCancelReason(criteria.getCancelReason()))
			.and(eqIsDeleted(criteria.getIsDeleted()));

		JPAQuery<Queue> query = queryFactory
			.selectFrom(queue)
			.where(conditions)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Queue.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		Long total = queryFactory
			.select(queue.id.count())
			.from(queue)
			.where(conditions)
			.fetchOne();
		assert total != null;

		return new PageImpl<>(query.fetch(), pageable, total);
	}

	@Override
	public Optional<Queue> findActiveHistoryById(Long id) {
		return Optional.ofNullable(queryFactory
			.selectFrom(queue)
			.where(
				eqId(id),
				eqIsDeleted(Boolean.FALSE)
			)
			.fetchOne()
		);
	}

	@Override
	public Optional<List<Queue>> findQueueHistoryByStatus(QueueStatus status, Boolean isDeleted) {
		List<Queue> history = queryFactory
			.selectFrom(queue)
			.where(
				eqStatus(status),
				eqIsDeleted(isDeleted)
			)
			.fetch();

		return Optional.ofNullable(history.isEmpty() ? null : history);
	}

	private BooleanExpression eqId(Long id) {
		if (id == null) {
			return null;
		}
		return queue.id.eq(id);
	}

	private BooleanExpression eqStoreId(Long storeId) {
		if (storeId == null) {
			return null;
		}
		return queue.storeId.eq(storeId);
	}

	private BooleanExpression eqUserId(Long userId) {
		if (userId == null) {
			return null;
		}
		return queue.userId.eq(userId);
	}

	private BooleanExpression eqStatus(QueueStatus status) {
		if (status == null) {
			return null;
		}
		return queue.status.eq(status);
	}

	private BooleanExpression eqCancelReason(CancelReason cancelReason) {
		if (cancelReason == null) {
			return null;
		}
		return queue.cancelReason.eq(cancelReason);
	}

	private BooleanExpression eqIsDeleted(Boolean isDeleted) {
		if (isDeleted == null) {
			return null;
		}
		return queue.isDeleted.eq(isDeleted);
	}

}
