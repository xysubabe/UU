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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 支付服务测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PaymentService 测试")
class PaymentServiceImplTest {

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private MockPaymentMapper mockPaymentMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderLogService orderLogService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Long testUserId;
    private Long testOrderId;
    private Long testPaymentId;
    private Order testOrder;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testUserId = 1001L;
        testOrderId = 2001L;
        testPaymentId = 3001L;

        // 创建测试订单
        testOrder = new Order();
        testOrder.setId(testOrderId);
        testOrder.setUserId(testUserId);
        testOrder.setOrderCode("UU2001");
        testOrder.setServiceType(com.uu.enums.ServiceTypeEnum.HELP_BUY);
        testOrder.setStatus(OrderStatusEnum.PENDING_PAYMENT);
        testOrder.setAmount(new BigDecimal("10.00"));
        testOrder.setDeliveryFee(new BigDecimal("10.00"));

        // 创建测试支付
        testPayment = new Payment();
        testPayment.setId(testPaymentId);
        testPayment.setOrderId(testOrderId);
        testPayment.setOrderCode("UU2001");
        testPayment.setAmount(new BigDecimal("10.00"));
        testPayment.setStatus(PaymentStatusEnum.PENDING);
    }

    @Test
    @DisplayName("创建支付 - 成功")
    void testCreatePayment_success() {
        // Given
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(testOrderId);
        request.setAmount(1000); // 10.00元

        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);
        when(paymentMapper.insert(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(testPaymentId);
            return 1;
        });

        // When
        PaymentResponse response = paymentService.createPayment(testUserId, request);

        // Then
        assertNotNull(response);
        assertEquals(testPaymentId.toString(), response.getPaymentId());
        assertNotNull(response.getPaymentParams());
        verify(paymentMapper).insert(argThat(payment ->
            payment.getOrderId().equals(testOrderId) &&
            payment.getStatus() == PaymentStatusEnum.PENDING &&
            payment.getAmount().equals(new BigDecimal("10.00"))
        ));
    }

    @Test
    @DisplayName("创建支付 - 订单不存在")
    void testCreatePayment_orderNotFound() {
        // Given
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(testOrderId);
        request.setAmount(1000);

        when(orderMapper.selectById(testOrderId)).thenReturn(null);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> paymentService.createPayment(testUserId, request));
        assertEquals(ErrorCodeEnum.ORDER_NOT_FOUND.getCode(), ex.getCode());
        verify(paymentMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建支付 - 订单状态不允许")
    void testCreatePayment_invalidOrderStatus() {
        // Given
        testOrder.setStatus(OrderStatusEnum.PENDING_ACCEPT);
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(testOrderId);
        request.setAmount(1000);

        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> paymentService.createPayment(testUserId, request));
        assertEquals(ErrorCodeEnum.ORDER_STATUS_ERROR.getCode(), ex.getCode());
        verify(paymentMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建支付 - 金额不匹配")
    void testCreatePayment_amountMismatch() {
        // Given
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(testOrderId);
        request.setAmount(1500); // 15.00元，但订单金额是10.00元

        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> paymentService.createPayment(testUserId, request));
        assertEquals(ErrorCodeEnum.PAYMENT_AMOUNT_MISMATCH.getCode(), ex.getCode());
        verify(paymentMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建支付 - 无权访问")
    void testCreatePayment_forbidden() {
        // Given
        Order otherUserOrder = new Order();
        otherUserOrder.setId(testOrderId);
        otherUserOrder.setUserId(9999L);
        otherUserOrder.setStatus(OrderStatusEnum.PENDING_PAYMENT);
        otherUserOrder.setAmount(new BigDecimal("10.00"));

        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(testOrderId);
        request.setAmount(1000);

        when(orderMapper.selectById(testOrderId)).thenReturn(otherUserOrder);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> paymentService.createPayment(testUserId, request));
        assertEquals(ErrorCodeEnum.FORBIDDEN.getCode(), ex.getCode());
        verify(paymentMapper, never()).insert(any());
    }

    @Test
    @DisplayName("处理支付回调 - 成功")
    void testHandleCallback_success() {
        // Given
        String transactionId = "WX_TRANSACTION_123";
        String orderCode = "UU2001";
        String amount = "10.00";

        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testPayment);
        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);
        when(paymentMapper.updateById(any(Payment.class))).thenReturn(1);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);
        when(orderLogService.createLog(anyLong(), anyString(), anyString())).thenReturn(1L);

        // When
        paymentService.handleCallback(transactionId, orderCode, amount);

        // Then
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentMapper, times(2)).updateById(paymentCaptor.capture());

        Payment capturedPayment = paymentCaptor.getAllValues().get(1);
        assertEquals(transactionId, capturedPayment.getTransactionId());
        assertEquals(PaymentStatusEnum.PAID, capturedPayment.getStatus());

        verify(orderMapper).updateById(argThat(order ->
            order.getStatus() == OrderStatusEnum.PENDING_ACCEPT &&
            order.getPaymentId().equals(testPaymentId)
        ));
        verify(orderLogService).createLog(testOrderId, "支付成功", "订单支付成功");
    }

    @Test
    @DisplayName("处理支付回调 - 幂等性（已支付）")
    void testHandleCallback_idempotent() {
        // Given
        testPayment.setStatus(PaymentStatusEnum.PAID);
        String transactionId = "WX_TRANSACTION_123";
        String orderCode = "UU2001";
        String amount = "10.00";

        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testPayment);

        // When
        paymentService.handleCallback(transactionId, orderCode, amount);

        // Then - 不应更新订单状态
        verify(orderMapper, never()).updateById(any(Order.class));
        verify(orderLogService, never()).createLog(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("处理支付回调 - 支付记录不存在")
    void testHandleCallback_paymentNotFound() {
        // Given
        String transactionId = "WX_TRANSACTION_123";
        String orderCode = "UU2001";
        String amount = "10.00";

        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> paymentService.handleCallback(transactionId, orderCode, amount));
        assertEquals(ErrorCodeEnum.PAYMENT_CALLBACK_FAILED.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("处理支付回调 - 金额不匹配")
    void testHandleCallback_amountMismatch() {
        // Given
        String transactionId = "WX_TRANSACTION_123";
        String orderCode = "UU2001";
        String amount = "15.00"; // 金额不匹配

        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testPayment);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> paymentService.handleCallback(transactionId, orderCode, amount));
        assertEquals(ErrorCodeEnum.PAYMENT_AMOUNT_MISMATCH.getCode(), ex.getCode());
        verify(orderMapper, never()).updateById(any(Order.class));
    }

    @Test
    @DisplayName("Mock支付 - DevOps用户成功")
    void testMockPay_devOpsUser_success() {
        // Given
        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);
        when(paymentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(paymentMapper.insert(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(testPaymentId);
            return 1;
        });
        when(mockPaymentMapper.insert(any(MockPayment.class))).thenReturn(1);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);
        when(orderLogService.createLog(anyLong(), anyString(), anyString())).thenReturn(1L);

        // When - DevOps ID = 999999
        PaymentResponse response = paymentService.mockPay(999999L, testOrderId);

        // Then
        assertNotNull(response);
        assertEquals(testPaymentId.toString(), response.getPaymentId());
        verify(orderMapper).updateById(argThat(order ->
            order.getStatus() == OrderStatusEnum.PENDING_ACCEPT &&
            order.getPaymentId().equals(testPaymentId)
        ));
    }

    @Test
    @DisplayName("Mock支付 - 订单不存在")
    void testMockPay_orderNotFound() {
        // Given
        when(orderMapper.selectById(testOrderId)).thenReturn(null);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> paymentService.mockPay(999999L, testOrderId));
        assertEquals(ErrorCodeEnum.ORDER_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("Mock支付 - 订单状态不允许")
    void testMockPay_invalidOrderStatus() {
        // Given
        testOrder.setStatus(OrderStatusEnum.IN_PROGRESS);
        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> paymentService.mockPay(999999L, testOrderId));
        assertEquals(ErrorCodeEnum.ORDER_STATUS_ERROR.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("Mock支付 - 普通用户无权访问")
    void testMockPay_regularUser_forbidden() {
        // Given - 创建其他用户的订单
        Order otherUserOrder = new Order();
        otherUserOrder.setId(testOrderId);
        otherUserOrder.setUserId(9999L); // 其他用户
        otherUserOrder.setOrderCode("UU2001");
        otherUserOrder.setStatus(OrderStatusEnum.PENDING_PAYMENT);
        otherUserOrder.setAmount(new BigDecimal("10.00"));

        when(orderMapper.selectById(testOrderId)).thenReturn(otherUserOrder);

        // When & Then - 普通用户不能操作其他用户的订单
        BusinessException ex = assertThrows(BusinessException.class,
            () -> paymentService.mockPay(testUserId, testOrderId));
        assertEquals(ErrorCodeEnum.FORBIDDEN.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("验证订单所有权 - 成功")
    void testValidateOrder_success() {
        // Given
        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);

        // When
        Order order = paymentService.validateOrder(testUserId, testOrderId);

        // Then
        assertNotNull(order);
        assertEquals(testOrderId, order.getId());
    }

    @Test
    @DisplayName("验证订单所有权 - 订单不存在")
    void testValidateOrder_notFound() {
        // Given
        when(orderMapper.selectById(testOrderId)).thenReturn(null);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> paymentService.validateOrder(testUserId, testOrderId));
        assertEquals(ErrorCodeEnum.ORDER_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("处理支付成功 - 订单状态已变更（幂等性）")
    void testProcessPaymentSuccess_orderStatusChanged() {
        // Given
        testOrder.setStatus(OrderStatusEnum.CANCELLED);
        when(paymentMapper.updateById(any(Payment.class))).thenReturn(1);

        // When
        paymentService.processPaymentSuccess(testOrder, testPayment, "TEST_TXN_123");

        // Then - 不应更新订单状态（因为订单已被取消）
        verify(orderMapper, never()).updateById(any(Order.class));
        verify(orderLogService, never()).createLog(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("处理支付成功 - 成功")
    void testProcessPaymentSuccess_success() {
        // Given
        when(paymentMapper.updateById(any(Payment.class))).thenReturn(1);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);
        when(orderLogService.createLog(anyLong(), anyString(), anyString())).thenReturn(1L);

        // When
        paymentService.processPaymentSuccess(testOrder, testPayment, "TEST_TXN_123");

        // Then
        verify(paymentMapper).updateById(argThat(payment ->
            payment.getTransactionId().equals("TEST_TXN_123") &&
            payment.getStatus() == PaymentStatusEnum.PAID
        ));
        verify(orderMapper).updateById(argThat(order ->
            order.getStatus() == OrderStatusEnum.PENDING_ACCEPT &&
            order.getPaymentId().equals(testPaymentId)
        ));
        verify(orderLogService).createLog(testOrderId, "支付成功", "订单支付成功");
    }

    @Test
    @DisplayName("转换为响应DTO")
    void testToResponse() {
        // When
        PaymentResponse response = paymentService.toResponse(testPayment);

        // Then
        assertNotNull(response);
        assertEquals(testPaymentId.toString(), response.getPaymentId());
        assertNotNull(response.getPaymentParams());
        assertTrue(response.getPaymentParams().containsKey("timeStamp"));
        assertTrue(response.getPaymentParams().containsKey("nonceStr"));
    }
}