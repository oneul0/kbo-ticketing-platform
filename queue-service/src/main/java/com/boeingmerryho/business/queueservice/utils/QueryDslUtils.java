package com.boeingmerryho.business.queueservice.utils;

import org.springframework.data.domain.Sort;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;

public class QueryDslUtils {

	public static OrderSpecifier<?>[] getOrderSpecifiers(Sort sort, Class<?> entityClass) {
		PathBuilder<?> entityPath = new PathBuilder<>(entityClass, entityClass.getSimpleName().toLowerCase());
		return sort.stream()
			.map(order -> new OrderSpecifier<>(
					order.isAscending() ? Order.ASC : Order.DESC,
					entityPath.getString(order.getProperty())
				)
			)
			.toList()
			.toArray(new OrderSpecifier[0]);
	}
}
