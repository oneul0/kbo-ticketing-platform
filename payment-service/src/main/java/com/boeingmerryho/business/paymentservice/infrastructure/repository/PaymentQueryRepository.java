package com.boeingmerryho.business.paymentservice.infrastructure.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.entity.QPaymentDetail;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentQueryRepository {
	private final JPAQueryFactory queryFactory;

	public Optional<PaymentDetail> findPaymentDetailById(Long id) {
		QPaymentDetail paymentDetail = QPaymentDetail.paymentDetail;

		return Optional.ofNullable(
			queryFactory.selectFrom(paymentDetail)
				.where(
					paymentDetail.id.eq(id)
				)
				.fetchOne()
		);
	}
}
