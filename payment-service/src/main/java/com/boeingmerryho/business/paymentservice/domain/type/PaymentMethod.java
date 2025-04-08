package com.boeingmerryho.business.paymentservice.domain.type;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    BANK_TRANSFER("무통장입금"),
    CARD("카드"),
    KAKAOPAY("카카오페이"),
    ;

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }
}
