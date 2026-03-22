package com.uu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {
    // 通用错误 (1xxx)
    SUCCESS(0, "成功"),
    UNKNOWN_ERROR(1000, "未知错误"),
    INVALID_PARAMS(1001, "参数错误"),
    UNAUTHORIZED(1002, "未授权"),
    FORBIDDEN(1003, "禁止访问"),
    NOT_FOUND(1004, "资源不存在"),

    // 用户错误 (2xxx)
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_DISABLED(2002, "用户已被禁用"),

    // 订单错误 (3xxx)
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态错误"),
    ORDER_ALREADY_PAID(3003, "订单已支付"),
    ORDER_CANCELLED(3004, "订单已取消"),
    ORDER_CANNOT_CANCEL(3005, "订单不可取消"),

    // 地址错误 (4xxx)
    ADDRESS_NOT_FOUND(4001, "地址不存在"),
    ADDRESS_LIMIT_EXCEEDED(4002, "地址数量超限"),

    // 支付错误 (5xxx)
    PAYMENT_FAILED(5001, "支付失败"),
    PAYMENT_CALLBACK_FAILED(5002, "支付回调失败"),
    PAYMENT_AMOUNT_MISMATCH(5003, "支付金额不匹配"),

    // 微信接口错误 (6xxx)
    WECHAT_LOGIN_FAILED(6001, "微信登录失败"),
    WECHAT_PAY_FAILED(6002, "微信支付失败");

    private final int code;
    private final String message;
}