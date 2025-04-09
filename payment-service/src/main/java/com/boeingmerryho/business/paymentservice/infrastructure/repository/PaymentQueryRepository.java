package com.boeingmerryho.business.paymentservice.infrastructure.repository;

import static com.boeingmerryho.business.paymentservice.domain.entity.QPayment.*;
import static com.boeingmerryho.business.paymentservice.domain.entity.QPaymentDetail.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.entity.QPaymentDetail;
import com.boeingmerryho.business.paymentservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentQueryRepository {
	private final JPAQueryFactory queryFactory;

	public Optional<PaymentDetail> findPaymentDetailByIdAndIsDeletedFalse(Long id) {

		return Optional.ofNullable(
			queryFactory.selectFrom(paymentDetail)
				.where(
					eqId(id),
					eqIsDeleted(Boolean.FALSE)
				)
				.fetchOne()
		);
	}

	public Optional<PaymentDetail> findPaymentDetailById(Long id) {
		QPaymentDetail paymentDetail = QPaymentDetail.paymentDetail;

		return Optional.ofNullable(
			queryFactory.selectFrom(paymentDetail)
				.where(
					eqId(id)
				)
				.fetchOne()
		);
	}

	public Page<PaymentDetail> searchPaymentDetail(PaymentDetailSearchContext context) {
		BooleanBuilder builder = new BooleanBuilder();
		builder
			.and(eqId(context.getId()))
			.and(eqPaymentId(context.getId()))
			.and(eqIsDeleted(context.getIsDeleted()));

		List<PaymentDetail> details = queryFactory.select(paymentDetail)
			.from(paymentDetail)
			.join(payment).on(paymentDetail.payment.eq(payment)).fetchJoin()
			.where(builder)
			.orderBy(QueryDslUtils.getOrderSpecifiers(context.getCustomPageable().getSort(), PaymentDetail.class))
			.offset(context.getCustomPageable().getOffset())
			.limit(context.getCustomPageable().getPageSize())
			.fetch();

		Long total = queryFactory.select(paymentDetail.count())
			.from(paymentDetail)
			.join(payment).on(paymentDetail.payment.eq(payment)).fetchJoin()
			.where(builder)
			.fetchOne();

		return new PageImpl<>(
			details,
			PageRequest.of(context.getCustomPageable().getPageNumber(), context.getCustomPageable().getPageSize()),
			total != null ? total : 0L
		);

	}

	private BooleanExpression eqIsDeleted(Boolean isDeleted) {
		if (isDeleted == null) {
			return null;
		}
		return paymentDetail.payment.isDeleted.eq(isDeleted);
	}

	private BooleanExpression eqPaymentId(Long paymentId) {
		if (paymentId == null) {
			return null;
		}
		return paymentDetail.payment.id.eq(paymentId);
	}

	private BooleanExpression eqUserId(Long userId) {
		if (userId == null) {
			return null;
		}
		return paymentDetail.payment.userId.eq(userId);

	}

	private BooleanExpression eqId(Long id) {
		if (id == null) {
			return null;
		}
		return paymentDetail.id.eq(id);

	}

}
