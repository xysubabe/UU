package com.uu.controller.payment;

import com.uu.dto.request.PaymentCreateRequest;
import com.uu.dto.response.ApiResponse;
import com.uu.dto.response.PaymentResponse;
import com.uu.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 支付控制器测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("PaymentController 测试")
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentResponse mockPaymentResponse;

    @BeforeEach
    void setUp() {
        mockPaymentResponse = new PaymentResponse();
        mockPaymentResponse.setPaymentId("3001");
    }

    @Test
    @DisplayName("创建支付 - 成功")
    void testCreatePayment_success() {
        // Given
        when(paymentService.createPayment(anyLong(), any(PaymentCreateRequest.class)))
            .thenReturn(mockPaymentResponse);

        // When
        ApiResponse<PaymentResponse> response = paymentController.createPayment(
                new PaymentCreateRequest(), null);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getCode());
        assertEquals("3001", response.getData().getPaymentId());
    }

    @Test
    @DisplayName("支付回调 - 成功")
    void testHandleCallback_success() {
        // Given
        doNothing().when(paymentService).handleCallback("TXN123", "UU2001", "10.00");

        // When
        String response = paymentController.handleCallback("TXN123", "UU2001", "10.00");

        // Then
        assertEquals("SUCCESS", response);
        verify(paymentService).handleCallback("TXN123", "UU2001", "10.00");
    }

    @Test
    @DisplayName("Mock支付 - 成功")
    void testMockPay_success() {
        // Given
        when(paymentService.mockPay(anyLong(), eq(2001L))).thenReturn(mockPaymentResponse);

        // When
        ApiResponse<PaymentResponse> response = paymentController.mockPay(2001L);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getCode());
        assertEquals("3001", response.getData().getPaymentId());
    }
}