package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatusEnum {
    NORMAL(1, "正常"),
    DISABLED(0, "禁用");

    private final Integer code;
    private final String desc;

    public static UserStatusEnum getByCode(Integer code) {
        if (code == null) return null;
        for (UserStatusEnum status : values()) {
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