package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceTypeEnum {
    HELP_BUY(1, "帮买"),
    HELP_SEND(2, "帮送"),
    HELP_QUEUE(3, "帮排队");

    private final Integer code;
    private final String desc;

    public static ServiceTypeEnum getByCode(Integer code) {
        if (code == null) return null;
        for (ServiceTypeEnum serviceType : values()) {
            if (serviceType.getCode().equals(code)) {
                return serviceType;
            }
        }
        return null;
    }

    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }
}