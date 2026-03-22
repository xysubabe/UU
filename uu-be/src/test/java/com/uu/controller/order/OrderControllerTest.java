package com.uu.controller.order;

import com.uu.dto.request.OrderCreateRequest;
import com.uu.dto.response.ApiResponse;
import com.uu.dto.response.IdStringResponse;
import com.uu.dto.response.OrderDetailResponse;
import com.uu.dto.response.OrderListResponse;
import com.uu.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
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
 * 订单控制器测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("OrderController 测试")
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        when(request.getAttribute("userId")).thenReturn(1001L);
    }

    @Test
    @DisplayName("创建订单 - 成功")
    void testCreateOrder_success() {
        // Given
        OrderCreateRequest createRequest = new OrderCreateRequest();
        createRequest.setServiceType(1);
        createRequest.setEndAddressId(3001L);
        createRequest.setAmount(1000);

        ApiResponse<IdStringResponse> expectedResponse = ApiResponse.success(IdStringResponse.of(2001L));
        when(orderService.createOrder(1001L, createRequest)).thenReturn(2001L);

        // When
        ApiResponse<IdStringResponse> response = orderController.createOrder(createRequest, request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getCode());
        assertEquals("2001", response.getData().getId());
        verify(orderService).createOrder(1001L, createRequest);
    }

    @Test
    @DisplayName("获取订单列表 - 成功")
    void testGetOrderList_success() {
        // Given
        OrderListResponse serviceResponse = new OrderListResponse();
        serviceResponse.setTotal(10);

        when(orderService.getOrderList(1001L, null, 1, 10)).thenReturn(serviceResponse);

        // When
        ApiResponse<OrderListResponse> response = orderController.getOrderList(null, 1, 10, request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getCode());
        assertEquals(10, response.getData().getTotal());
    }

    @Test
    @DisplayName("获取进行中订单 - 成功")
    void testGetOngoingOrders_success() {
        // Given
        OrderListResponse serviceResponse = new OrderListResponse();
        serviceResponse.setTotal(3);

        when(orderService.getOngoingOrders(1001L, 1, 10)).thenReturn(serviceResponse);

        // When
        ApiResponse<OrderListResponse> response = orderController.getOngoingOrders(1, 10, request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getCode());
        assertEquals(3, response.getData().getTotal());
    }

    @Test
    @DisplayName("获取订单详情 - 成功")
    void testGetOrderDetail_success() {
        // Given
        OrderDetailResponse detailResponse = new OrderDetailResponse();
        detailResponse.setId("2001");

        when(orderService.getOrderDetail(1001L, 2001L)).thenReturn(detailResponse);

        // When
        ApiResponse<OrderDetailResponse> response = orderController.getOrderDetail(2001L, request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getCode());
        assertEquals("2001", response.getData().getId());
    }

    @Test
    @DisplayName("取消订单 - 成功")
    void testCancelOrder_success() {
        // Given
        doNothing().when(orderService).cancelOrder(1001L, 2001L);

        // When
        ApiResponse<Void> response = orderController.cancelOrder(2001L, request);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getCode());
        verify(orderService).cancelOrder(1001L, 2001L);
    }
}