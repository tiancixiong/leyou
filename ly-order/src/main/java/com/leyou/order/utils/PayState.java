package com.leyou.order.utils;

/**
 * 支付状态枚举
 */
public enum PayState {
    NOT_PAY(0), SUCCESS(1), FAIL(2);

    PayState(int value) {
        this.value = value;
    }

    int value;

    public int getValue() {
        return value;
    }
}
