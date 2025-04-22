package com.boeingmerryho.business.paymentservice.application.factory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.boeingmerryho.business.paymentservice.application.service.PaymentStrategy;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;
import com.boeingmerryho.business.paymentservice.presentation.code.PaymentErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStrategyFactory {

	private final Map<PaymentMethod, PaymentStrategy> strategyMap;

	@Autowired
	public PaymentStrategyFactory(List<PaymentStrategy> strategies) {
		this.strategyMap = strategies.stream()
			.collect(Collectors.toMap(
				PaymentStrategy::getSupportedMethod,
				Function.identity()
			));
	}

	public PaymentStrategy getStrategy(String methodName) {
		PaymentMethod method = PaymentMethod.from(methodName);
		return getStrategy(method);
	}

	public PaymentStrategy getStrategy(PaymentMethod method) {
		PaymentStrategy strategy = strategyMap.get(method);
		if (strategy == null) {
			throw new PaymentException(PaymentErrorCode.PAYMENT_UNSUPPORTED);
		}
		return strategy;
	}
}

