package br.com.joaobarbosa.modules.orders;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("pending"),
    PAID("paid"),
    CANCELLED("cancelled");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }
}
