package com.uu.service;

import com.uu.dto.request.PaymentCreateRequest;
import com.uu.dto.response.PaymentResponse;
import com.uu.entity.Payment;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付
     * @param userId 用户ID
     * @param request 创建请求
     * @return 支付响应
     */
    PaymentResponse createPayment(Long userId, PaymentCreateRequest request);

    /**
     * 处理支付回调
     * @param transactionId 第三方交易ID
     * @param orderCode 订单编号
     * @param amount 支付金额
     */
    void handleCallback(String transactionId, String orderCode, String amount);

    /**
     * Mock支付（运维专用）
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 支付响应
     */
    PaymentResponse mockPay(Long userId, Long orderId);

    /**
     * 验证订单所有权和状态
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 订单实体
     */
    com.uu.entity.Order validateOrder(Long userId, Long orderId);

    /**
     * 支付成功处理
     * @param order 订单实体
     * @param payment 支付实体
     * @param transactionId 第三方交易ID
     */
    void processPaymentSuccess(com.uu.entity.Order order, Payment payment, String transactionId);

    /**
     * 转换为响应DTO
     * @param payment 支付实体
     * @return 响应DTO
     */
    PaymentResponse toResponse(Payment payment);
}