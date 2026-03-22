package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    PENDING_PAYMENT(100, "待支付"),
    PENDING_ACCEPT(200, "待接单"),
    IN_PROGRESS(300, "进行中"),
    COMPLETED(600, "已完成"),
    CANCELLED(999, "已取消");

    private final Integer code;
    private final String desc;

    public static OrderStatusEnum getByCode(Integer code) {
        if (code == null) return null;
        for (OrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }

    public boolean canCancel() {
        return this == PENDING_PAYMENT || this == PENDING_ACCEPT;
    }
}