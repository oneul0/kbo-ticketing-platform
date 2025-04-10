package com.boeingmerryho.business.paymentservice.domain.entity;

import com.boeingmerryho.business.paymentservice.domain.type.PaymentMethod;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "p_payment_detail")
public class PaymentDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "tid", column = @Column(name = "tid")),
		@AttributeOverride(name = "cid", column = @Column(name = "cid"))
	})
	private KakaoPayInfo kakaoPayInfo;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id")
	private Payment payment;

	@Column(nullable = false)
	private Integer discountPrice;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentMethod method;

	@Column(nullable = false)
	private Integer discountAmount;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "accountNumber", column = @Column(name = "account_number")),
		@AttributeOverride(name = "accountBank", column = @Column(name = "account_bank")),
		@AttributeOverride(name = "dueDate", column = @Column(name = "due_date")),
		@AttributeOverride(name = "accountHolder", column = @Column(name = "account_holder"))
	})
	private VirtualAccountInfo accountInfo;

}
