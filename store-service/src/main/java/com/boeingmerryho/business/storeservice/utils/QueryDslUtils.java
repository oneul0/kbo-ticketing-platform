package com.boeingmerryho.business.storeservice.utils;

import org.springframework.data.domain.Sort;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;

public class QueryDslUtils {

	public static OrderSpecifier<?>[] getOrderSpecifiers(Sort sort, Class<?> entityClass) {
		String str = entityClass.getSimpleName();
		PathBuilder<?> entityPath = new PathBuilder<>(entityClass,
			Character.toLowerCase(str.charAt(0)) + str.substring(1));

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