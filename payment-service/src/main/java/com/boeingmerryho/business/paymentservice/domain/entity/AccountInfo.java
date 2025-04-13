package com.boeingmerryho.business.paymentservice.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountInfo {
	private String accountNumber;
	private String accountBank;
	private LocalDateTime dueDate;
	private String accountHolder;
}
