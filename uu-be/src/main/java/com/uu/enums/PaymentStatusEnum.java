package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatusEnum {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    REFUNDED(2, "已退款");

    private final Integer code;
    private final String desc;

    public static PaymentStatusEnum getByCode(Integer code) {
        if (code == null) return null;
        for (PaymentStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }
}