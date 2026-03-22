package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum YesNoStatusEnum {
    NO(0, "否"),
    YES(1, "是");

    private final Integer code;
    private final String desc;

    public static YesNoStatusEnum getByCode(Integer code) {
        if (code == null) return NO;
        for (YesNoStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return NO;
    }

    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }

    public Boolean toBoolean() {
        return this == YES;
    }

    public static YesNoStatusEnum fromBoolean(Boolean value) {
        if (value == null) return NO;
        return value ? YES : NO;
    }
}