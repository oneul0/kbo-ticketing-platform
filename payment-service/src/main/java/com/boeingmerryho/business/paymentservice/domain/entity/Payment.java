package com.boeingmerryho.business.paymentservice.domain.entity;

import com.boeingmerryho.business.paymentservice.domain.type.DiscountType;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentStatus;
import com.boeingmerryho.business.paymentservice.domain.type.PaymentType;

import io.github.boeingmerryho.commonlibrary.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_payment")
public class Payment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Integer totalPrice;

	@Column(nullable = false)
	private Integer discountPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private PaymentStatus status = PaymentStatus.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private DiscountType discountType = DiscountType.NONE;

	public void confirmPayment() {
		this.status = PaymentStatus.CONFIRMED;
	}

	public void requestCancel() {
		this.status = PaymentStatus.REFUND_REQUESTED;
	}

	public boolean validateStatus(PaymentStatus status) {
		return this.status == status;
	}

	public boolean validateType(PaymentType type) {
		return this.type == type;
	}
}
