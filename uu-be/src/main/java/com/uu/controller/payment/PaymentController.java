package com.uu.controller.payment;

import com.uu.dto.request.PaymentCreateRequest;
import com.uu.dto.response.ApiResponse;
import com.uu.dto.response.PaymentResponse;
import com.uu.interceptor.LoginInterceptor;
import com.uu.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付
     */
    @PostMapping("/create")
    public ApiResponse<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = LoginInterceptor.getUserId(httpRequest);
        log.info("创建支付, userId={}, orderId={}", userId, request.getOrderId());
        PaymentResponse response = paymentService.createPayment(userId, request);
        return ApiResponse.success(response);
    }

    /**
     * 支付回调
     * 说明：此接口由微信支付服务器调用，不需要登录校验
     */
    @PostMapping("/callback")
    public String handleCallback(
            @RequestParam String transactionId,
            @RequestParam String orderCode,
            @RequestParam String amount) {
        log.info("收到支付回调, orderCode={}, transactionId={}", orderCode, transactionId);
        paymentService.handleCallback(transactionId, orderCode, amount);
        return "SUCCESS";
    }

    /**
     * Mock支付（运维专用）
     */
    @PostMapping("/mock-pay")
    public ApiResponse<PaymentResponse> mockPay(
            @RequestParam Long orderId,
            HttpServletRequest request) {
        Long userId = LoginInterceptor.getUserId(request);
        log.info("Mock支付, userId={}, orderId={}", userId, orderId);
        PaymentResponse response = paymentService.mockPay(userId, orderId);
        return ApiResponse.success(response);
    }
}