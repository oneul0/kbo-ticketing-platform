package com.boeingmerryho.business.paymentservice.infrastructure.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boeingmerryho.business.paymentservice.application.dto.PaymentApplicationMapper;
import com.boeingmerryho.business.paymentservice.application.dto.kakao.PaymentSession;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentApproveRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentDetailSearchRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentMembershipCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentReadyRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.request.PaymentTicketCancelRequestServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentApproveResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentDetailResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentMembershipCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentReadyResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.dto.response.PaymentTicketCancelResponseServiceDto;
import com.boeingmerryho.business.paymentservice.application.factory.PaymentStrategyFactory;
import com.boeingmerryho.business.paymentservice.application.service.PaymentService;
import com.boeingmerryho.business.paymentservice.application.strategy.PaymentStrategy;
import com.boeingmerryho.business.paymentservice.domain.context.PaymentDetailSearchContext;
import com.boeingmerryho.business.paymentservice.domain.entity.Payment;
import com.boeingmerryho.business.paymentservice.domain.entity.PaymentDetail;
import com.boeingmerryho.business.paymentservice.domain.helper.PaymentReader;
import com.boeingmerryho.business.paymentservice.domain.type.DiscountType;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentStatus;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;
import com.boeingmerryho.business.paymentservice.infrastructure.exception.PaymentException;
import com.boeingmerryho.business.paymentservice.infrastructure.helper.MembershipApiClient;
import com.boeingmerryho.business.paymentservice.infrastructure.helper.PaySessionHelper;
import com.boeingmerryho.business.paymentservice.presentation.code.PaymentErrorCode;
import com.boeingmerryho.business.paymentservice.presentation.dto.request.Ticket;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentReader paymentReader;
	private final PaySessionHelper paySessionHelper;
	private final PaymentStrategyFactory strategyFactory;
	private final MembershipApiClient membershipApiClient;
	private final PaymentApplicationMapper paymentApplicationMapper;

	@Override
	@Transactional
	public PaymentReadyResponseServiceDto pay(
		PaymentReadyRequestServiceDto requestServiceDto
	) {
		assertInExpiredTimePayment(requestServiceDto.paymentId());
		Payment payment = paymentReader.getPayment(requestServiceDto.paymentId());
		assertAvailablePayment(
			payment,
			requestServiceDto.userId(),
			requestServiceDto.price(),
			requestServiceDto.tickets()
		);
		calculateDiscountPrice(requestServiceDto, payment);
		PaymentStrategy strategy = strategyFactory.getStrategy(requestServiceDto.method());
		return strategy.pay(payment, requestServiceDto);
	}

	@Override
	@Transactional
	public PaymentApproveResponseServiceDto approvePayment(
		PaymentApproveRequestServiceDto requestServiceDto
	) {
		PaymentSession paymentSession = getPaymentSession(requestServiceDto);
		Payment payment = paymentReader.getPayment(requestServiceDto.paymentId());
		PaymentStrategy strategy = strategyFactory.getStrategy(paymentSession.method());
		return strategy.approve(
			paymentSession,
			payment,
			requestServiceDto
		);
	}

	@Override
	@Transactional
	public PaymentTicketCancelResponseServiceDto cancelTicketPayment(
		PaymentTicketCancelRequestServiceDto requestServiceDto
	) {
		Payment payment = paymentReader.getPayment(requestServiceDto.id());
		assertCancellablePayment(
			payment,
			requestServiceDto.userId()
		);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentTicketCancelResponseServiceDto(payment.getId());
	}

	@Override
	@Transactional
	public PaymentMembershipCancelResponseServiceDto cancelMembershipPayment(
		PaymentMembershipCancelRequestServiceDto requestServiceDto
	) {
		Payment payment = paymentReader.getPayment(requestServiceDto.id());
		assertCancellablePayment(
			payment,
			requestServiceDto.userId()
		);
		payment.requestCancel();
		return paymentApplicationMapper.toPaymentMembershipCancelResponseServiceDto(payment.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentDetailResponseServiceDto getPaymentDetail(
		PaymentDetailRequestServiceDto requestServiceDto
	) {
		PaymentDetail paymentDetail = paymentReader.getDetail(requestServiceDto.id());
		return paymentApplicationMapper.toPaymentDetailResponseServiceDto(paymentDetail);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentDetailResponseServiceDto> searchPaymentDetail(
		PaymentDetailSearchRequestServiceDto requestServiceDto
	) {
		PaymentDetailSearchContext searchContext = createSearchContext(requestServiceDto);
		Page<PaymentDetail> paymentDetails = paymentReader.getPaymentDetails(searchContext);
		return paymentDetails.map(paymentApplicationMapper::toPaymentDetailResponseServiceDto);
	}

	private PaymentSession getPaymentSession(
		PaymentApproveRequestServiceDto requestServiceDto
	) {
		return paySessionHelper.getPaymentInfo(String.valueOf(requestServiceDto.paymentId()))
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_INFO_NOT_FOUND));
	}

	private void assertInExpiredTimePayment(Long paymentId) {
		LocalDateTime expiredTime = paySessionHelper.getPaymentExpiredTime(paymentId.toString())
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));
		if (!LocalDateTime.now().isBefore(expiredTime)) {
			paySessionHelper.deletePaymentExpiredTime(paymentId.toString());
			throw new PaymentException(PaymentErrorCode.PAYMENT_EXPIRED);
		}
	}

	private void assertAvailablePayment(
		Payment payment,
		Long userId,
		int inputPrice,
		List<Ticket> tickets
	) {
		if (!payment.validateUser(userId)) {
			throw new PaymentException(PaymentErrorCode.UNAUTHORIZED);
		}
		if (!payment.validateStatus(PaymentStatus.PENDING)) {
			throw new PaymentException(PaymentErrorCode.PAYMENT_INVALID);
		}
		int price = paySessionHelper.getPaymentPrice(payment.getId().toString())
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_INVALID));
		int quantity = tickets == null ? 1 : tickets.size();
		if (inputPrice != price * quantity) {
			throw new PaymentException(PaymentErrorCode.PAYMENT_INVALID);
		}
	}

	private void assertCancellablePayment(
		Payment payment,
		Long userId
	) {
		if (!payment.validateUser(userId)) {
			throw new PaymentException(PaymentErrorCode.UNAUTHORIZED);
		}
		if (!payment.validateStatus(PaymentStatus.CONFIRMED)) {
			throw new PaymentException(PaymentErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
		if (!payment.validateType(PaymentType.TICKET)) {
			throw new PaymentException(PaymentErrorCode.PAYMENT_REFUND_REQUEST_FAIL);
		}
	}

	private void calculateDiscountPrice(
		PaymentReadyRequestServiceDto requestServiceDto,
		Payment payment
	) {
		Double discount = membershipApiClient.getDiscount(requestServiceDto.userId())
			.orElseThrow(() -> new PaymentException(PaymentErrorCode.MEMBERSHIP_SERVICE_UNAVAILABLE));
		payment.updateDiscountInfo(
			discount,
			DiscountType.from(requestServiceDto.discountType())
		);
	}

	private PaymentDetailSearchContext createSearchContext(
		PaymentDetailSearchRequestServiceDto requestServiceDto) {
		return PaymentDetailSearchContext.builder()
			.id(requestServiceDto.id())
			.paymentId(requestServiceDto.paymentId())
			.customPageable(requestServiceDto.customPageable())
			.isDeleted(requestServiceDto.isDeleted())
			.build();
	}
}
