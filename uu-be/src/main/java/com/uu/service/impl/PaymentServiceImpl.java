package com.uu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uu.dto.request.PaymentCreateRequest;
import com.uu.dto.response.PaymentResponse;
import com.uu.entity.MockPayment;
import com.uu.entity.Order;
import com.uu.entity.Payment;
import com.uu.enums.ErrorCodeEnum;
import com.uu.enums.OrderStatusEnum;
import com.uu.enums.PaymentStatusEnum;
import com.uu.exception.BusinessException;
import com.uu.mapper.MockPaymentMapper;
import com.uu.mapper.OrderMapper;
import com.uu.mapper.PaymentMapper;
import com.uu.service.OrderLogService;
import com.uu.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付服务实现
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private MockPaymentMapper mockPaymentMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderLogService orderLogService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse createPayment(Long userId, PaymentCreateRequest request) {
        // 验证订单所有权和状态
        Order order = validateOrder(userId, request.getOrderId());

        // 验证订单状态
        if (order.getStatus() != OrderStatusEnum.PENDING_PAYMENT) {
            throw new BusinessException(ErrorCodeEnum.ORDER_STATUS_ERROR);
        }

        // 验证支付金额
        BigDecimal orderAmount = order.getAmount();
        BigDecimal paymentAmount = new BigDecimal(request.getAmount());
        if (orderAmount.compareTo(paymentAmount) != 0) {
            throw new BusinessException(ErrorCodeEnum.PAYMENT_AMOUNT_MISMATCH);
        }

        // 创建支付记录
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setOrderCode(order.getOrderCode());
        payment.setAmount(paymentAmount);
        payment.setStatus(PaymentStatusEnum.PENDING);

        paymentMapper.insert(payment);

        log.info("创建支付成功, userId={}, orderId={}, paymentId={}", userId, order.getId(), payment.getId());
        return toResponse(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleCallback(String transactionId, String orderCode, String amount) {
        LambdaQueryWrapper<Payment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Payment::getOrderCode, orderCode)
                .eq(Payment::getStatus, PaymentStatusEnum.PENDING);

        Payment payment = paymentMapper.selectOne(queryWrapper);
        if (payment == null) {
            log.warn("支付回调失败，未找到待支付记录, orderCode={}", orderCode);
            throw new BusinessException(ErrorCodeEnum.PAYMENT_CALLBACK_FAILED);
        }

        // 验证金额
        BigDecimal callbackAmount = new BigDecimal(amount);
        if (payment.getAmount().compareTo(callbackAmount) != 0) {
            log.warn("支付回调失败，金额不匹配, orderCode={}, expected={}, actual={}",
                    orderCode, payment.getAmount(), callbackAmount);
            throw new BusinessException(ErrorCodeEnum.PAYMENT_AMOUNT_MISMATCH);
        }

        // 更新支付状态
        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatusEnum.PAID);
        paymentMapper.updateById(payment);

        // 获取订单
        Order order = orderMapper.selectById(payment.getOrderId());
        if (order == null) {
            log.error("支付回调失败，订单不存在, orderId={}", payment.getOrderId());
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND);
        }

        // 处理支付成功
        processPaymentSuccess(order, payment, transactionId);

        log.info("支付回调处理成功, orderCode={}, transactionId={}", orderCode, transactionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse mockPay(Long userId, Long orderId) {
        // 验证订单所有权和状态
        Order order = validateOrder(userId, orderId);

        // 只有待支付状态才允许Mock支付
        if (order.getStatus() != OrderStatusEnum.PENDING_PAYMENT) {
            throw new BusinessException(ErrorCodeEnum.ORDER_STATUS_ERROR);
        }

        // 查找或创建支付记录
        Payment payment = paymentMapper.selectOne(
                new LambdaQueryWrapper<Payment>()
                        .eq(Payment::getOrderId, orderId)
                        .eq(Payment::getStatus, PaymentStatusEnum.PENDING)
        );

        if (payment == null) {
            // 创建支付记录
            payment = new Payment();
            payment.setOrderId(order.getId());
            payment.setOrderCode(order.getOrderCode());
            payment.setAmount(order.getAmount());
            payment.setStatus(PaymentStatusEnum.PENDING);
            paymentMapper.insert(payment);
        }

        // 创建Mock支付记录
        MockPayment mockPayment = new MockPayment();
        mockPayment.setOrderId(order.getId());
        mockPayment.setOrderCode(order.getOrderCode());
        mockPayment.setAmount(order.getAmount());
        mockPayment.setStatus(PaymentStatusEnum.PENDING);

        try {
            Map<String, Object> mockResult = new HashMap<>();
            mockResult.put("mock", true);
            mockResult.put("success", true);
            mockResult.put("message", "Mock支付成功");
            mockPayment.setMockResult(objectMapper.writeValueAsString(mockResult));
        } catch (JsonProcessingException e) {
            log.error("Mock结果序列化失败", e);
        }

        mockPaymentMapper.insert(mockPayment);

        // 处理Mock支付成功
        processPaymentSuccess(order, payment, "MOCK_" + System.currentTimeMillis());

        // 更新Mock支付状态
        mockPayment.setStatus(PaymentStatusEnum.PAID);
        mockPaymentMapper.updateById(mockPayment);

        log.info("Mock支付成功, userId={}, orderId={}, paymentId={}", userId, orderId, payment.getId());
        return toResponse(payment);
    }

    @Override
    public Order validateOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND);
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN);
        }

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processPaymentSuccess(Order order, Payment payment, String transactionId) {
        // 更新支付状态
        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatusEnum.PAID);
        paymentMapper.updateById(payment);

        // 更新订单状态
        order.setStatus(OrderStatusEnum.PENDING_ACCEPT);
        order.setPaymentId(payment.getId());
        orderMapper.updateById(order);

        // 创建订单日志
        orderLogService.createLog(order.getId(), "支付成功", "订单支付成功");

        log.info("支付成功处理完成, orderId={}, paymentId={}", order.getId(), payment.getId());
    }

    @Override
    public PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId().toString());

        // 模拟支付参数（实际应调用微信支付API获取）
        Map<String, Object> paymentParams = new HashMap<>();
        paymentParams.put("timeStamp", System.currentTimeMillis() / 1000);
        paymentParams.put("nonceStr", "nonce_" + System.currentTimeMillis());
        paymentParams.put("package", "prepay_id=" + payment.getId());
        paymentParams.put("signType", "MD5");
        paymentParams.put("paySign", "mock_sign_" + payment.getId());
        response.setPaymentParams(paymentParams);

        return response;
    }
}