package com.uu.util;

import com.uu.exception.BusinessException;
import com.uu.enums.ErrorCodeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ValidatorUtil {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final int MIN_AMOUNT = 5;
    private static final int MAX_AMOUNT = 9999;

    public static void validatePhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, "手机号不能为空");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, "手机号格式不正确");
        }
    }

    public static void validateAmount(Integer amount) {
        if (amount == null) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, "跑腿费用不能为空");
        }
        if (amount < MIN_AMOUNT) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, "跑腿费用最小为" + MIN_AMOUNT + "元");
        }
        if (amount > MAX_AMOUNT) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, "跑腿费用最大为" + MAX_AMOUNT + "元");
        }
    }

    public static void validateStringLength(String value, int maxLength, String fieldName) {
        if (StringUtils.isNotBlank(value) && value.length() > maxLength) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS,
                fieldName + "不能超过" + maxLength + "个字符");
        }
    }

    public static void validateRequired(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, fieldName + "不能为空");
        }
    }

    public static void validateSnowflakeId(String id, String fieldName) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, fieldName + "不能为空");
        }
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS, fieldName + "格式不正确");
        }
    }
}