package com.uu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 支付响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    /**
     * 支付ID
     */
    private String paymentId;

    /**
     * 支付参数（用于前端拉起支付）
     */
    private Map<String, Object> paymentParams;
}