package com.boeingmerryho.business.paymentservice.domain.type;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("결제 전"),
    CONFIRMED("결제 완료"),
    REFUNDED("환불 완료"),
    ;

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}
