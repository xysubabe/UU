package com.uu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建支付请求DTO
 */
@Data
public class PaymentCreateRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 支付金额（单位：分）
     */
    @NotNull(message = "支付金额不能为空")
    private Integer amount;
}