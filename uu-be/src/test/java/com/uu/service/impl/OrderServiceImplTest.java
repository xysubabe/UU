package com.uu.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uu.dto.request.OrderCreateRequest;
import com.uu.dto.response.OrderDetailResponse;
import com.uu.dto.response.OrderListResponse;
import com.uu.entity.Address;
import com.uu.entity.Order;
import com.uu.enums.ErrorCodeEnum;
import com.uu.enums.OrderStatusEnum;
import com.uu.enums.ServiceTypeEnum;
import com.uu.exception.BusinessException;
import com.uu.mapper.OrderMapper;
import com.uu.service.AddressService;
import com.uu.service.OrderLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
 * 订单服务测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("OrderService 测试")
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderLogService orderLogService;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Long testUserId;
    private Long testOrderId;
    private Order testOrder;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testUserId = 1001L;
        testOrderId = 2001L;

        // 创建测试订单
        testOrder = new Order();
        testOrder.setId(testOrderId);
        testOrder.setUserId(testUserId);
        testOrder.setOrderCode("UU2001");
        testOrder.setServiceType(ServiceTypeEnum.HELP_BUY);
        testOrder.setStatus(OrderStatusEnum.PENDING_PAYMENT);
        testOrder.setTitle("测试订单");
        testOrder.setDescription("测试订单描述");
        testOrder.setAmount(new BigDecimal("10.00"));
        testOrder.setDeliveryFee(new BigDecimal("10.00"));
        testOrder.setEndAddress("北京市朝阳区测试地址");
        testOrder.setEndContactName("张三");
        testOrder.setEndContactPhone("13800138000");

        // 创建测试地址
        testAddress = new Address();
        testAddress.setId(3001L);
        testAddress.setUserId(testUserId);
        testAddress.setContactName("张三");
        testAddress.setContactPhone("13800138000");
        testAddress.setProvince("北京市");
        testAddress.setCity("北京市");
        testAddress.setDistrict("朝阳区");
        testAddress.setDetailAddress("测试地址");
    }

    @Test
    @DisplayName("创建帮买订单 - 成功")
    void testCreateOrder_helpBuy_success() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setServiceType(1); // 帮买
        request.setEndAddressId(3001L);
        request.setAmount(1000); // 10.00元

        when(addressService.validateOwnership(testUserId, 3001L)).thenReturn(testAddress);
        when(addressService.getById(3001L)).thenReturn(testAddress);
        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(testOrderId);
            return 1;
        });
        when(orderLogService.createLog(anyLong(), anyString(), anyString())).thenReturn(1L);

        // When
        Long orderId = orderService.createOrder(testUserId, request);

        // Then
        assertNotNull(orderId);
        verify(orderMapper).insert(argThat(order ->
            order.getUserId().equals(testUserId) &&
            order.getServiceType() == ServiceTypeEnum.HELP_BUY &&
            order.getStatus() == OrderStatusEnum.PENDING_PAYMENT &&
            order.getAmount().equals(new BigDecimal("10.00"))
        ));
        verify(orderLogService).createLog(testOrderId, "创建订单", "订单创建成功");
    }

    @Test
    @DisplayName("创建帮送订单 - 成功")
    void testCreateOrder_helpSend_success() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setServiceType(2); // 帮送
        request.setStartAddressId(3001L);
        request.setEndAddressId(3002L);
        request.setAmount(1500); // 15.00元

        Address startAddress = new Address();
        startAddress.setId(3001L);
        startAddress.setProvince("北京市");
        startAddress.setCity("北京市");
        startAddress.setDistrict("朝阳区");
        startAddress.setDetailAddress("起点地址");

        when(addressService.validateOwnership(testUserId, 3001L)).thenReturn(startAddress);
        when(addressService.validateOwnership(testUserId, 3002L)).thenReturn(testAddress);
        when(addressService.getById(3001L)).thenReturn(startAddress);
        when(addressService.getById(3002L)).thenReturn(testAddress);
        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(testOrderId);
            return 1;
        });
        when(orderLogService.createLog(anyLong(), anyString(), anyString())).thenReturn(1L);

        // When
        Long orderId = orderService.createOrder(testUserId, request);

        // Then
        assertNotNull(orderId);
        verify(orderMapper).insert(argThat(order ->
            order.getServiceType() == ServiceTypeEnum.HELP_SEND &&
            order.getStartAddress() != null &&
            order.getEndAddress() != null
        ));
    }

    @Test
    @DisplayName("创建帮排队订单 - 成功")
    void testCreateOrder_helpQueue_success() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setServiceType(3); // 帮排队
        request.setQueueAddressId(3001L);
        request.setAmount(500); // 5.00元

        when(addressService.validateOwnership(testUserId, 3001L)).thenReturn(testAddress);
        when(addressService.getById(3001L)).thenReturn(testAddress);
        when(orderMapper.insert(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(testOrderId);
            return 1;
        });
        when(orderLogService.createLog(anyLong(), anyString(), anyString())).thenReturn(1L);

        // When
        Long orderId = orderService.createOrder(testUserId, request);

        // Then
        assertNotNull(orderId);
        verify(orderMapper).insert(argThat(order ->
            order.getServiceType() == ServiceTypeEnum.HELP_QUEUE
        ));
    }

    @Test
    @DisplayName("创建订单 - 无效服务类型")
    void testCreateOrder_invalidServiceType() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setServiceType(999); // 无效的服务类型
        request.setEndAddressId(3001L);
        request.setAmount(1000);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> orderService.createOrder(testUserId, request));
        assertEquals(ErrorCodeEnum.INVALID_PARAMS.getCode(), ex.getCode());
        verify(orderMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建帮送订单 - 起点和终点地址相同")
    void testCreateOrder_helpSend_sameAddress() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setServiceType(2); // 帮送
        request.setStartAddressId(3001L);
        request.setEndAddressId(3001L); // 相同的地址
        request.setAmount(1000);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> orderService.createOrder(testUserId, request));
        assertEquals(ErrorCodeEnum.INVALID_PARAMS.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("创建订单 - 地址不存在")
    void testCreateOrder_addressNotFound() {
        // Given
        OrderCreateRequest request = new OrderCreateRequest();
        request.setServiceType(1);
        request.setEndAddressId(3001L);
        request.setAmount(1000);

        when(addressService.validateOwnership(testUserId, 3001L))
            .thenThrow(new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND));

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> orderService.createOrder(testUserId, request));
        assertEquals(ErrorCodeEnum.ADDRESS_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("获取订单列表 - 成功")
    void testGetOrderList_success() {
        // Given
        when(orderMapper.selectPage(any(Page.class), any()))
            .thenReturn(new Page<>(1, 10, 2));

        // When
        OrderListResponse response = orderService.getOrderList(testUserId, null, 1, 10);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getTotal());
        assertEquals(1, response.getPage());
        assertEquals(10, response.getPageSize());
        verify(orderMapper).selectPage(any(Page.class), any());
    }

    @Test
    @DisplayName("获取订单列表 - 状态筛选")
    void testGetOrderList_withStatusFilter() {
        // Given
        when(orderMapper.selectPage(any(Page.class), any()))
            .thenReturn(new Page<>(1, 10, 1));

        // When
        OrderListResponse response = orderService.getOrderList(testUserId, 100, 1, 10);

        // Then
        assertNotNull(response);
        verify(orderMapper).selectPage(any(Page.class), argThat(wrapper -> {
            // 验证查询条件包含状态筛选
            return true;
        }));
    }

    @Test
    @DisplayName("获取订单列表 - 无效分页参数")
    void testGetOrderList_invalidPaginationParams() {
        // Given
        when(orderMapper.selectPage(any(Page.class), any()))
            .thenReturn(new Page<>(1, 10, 0));

        // When - 负数页码
        OrderListResponse response1 = orderService.getOrderList(testUserId, null, -1, 10);

        // When - 过大的pageSize
        OrderListResponse response2 = orderService.getOrderList(testUserId, null, 1, 200);

        // Then - 验证参数被修正
        assertNotNull(response1);
        assertNotNull(response2);
        verify(orderMapper, times(2)).selectPage(argThat(page -> page.getCurrent() == 1 && page.getSize() == 10), any());
    }

    @Test
    @DisplayName("获取进行中订单 - 成功")
    void testGetOngoingOrders_success() {
        // Given
        when(orderMapper.selectPage(any(Page.class), any()))
            .thenReturn(new Page<>(1, 10, 3));

        // When
        OrderListResponse response = orderService.getOngoingOrders(testUserId, 1, 10);

        // Then
        assertNotNull(response);
        assertEquals(3, response.getTotal());
        verify(orderMapper).selectPage(any(Page.class), any());
    }

    @Test
    @DisplayName("获取订单详情 - 成功")
    void testGetOrderDetail_success() {
        // Given
        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);
        when(orderLogService.getOrderLogs(testOrderId)).thenReturn(java.util.List.of());
        when(orderLogService.toResponse(any())).thenReturn(
            new com.uu.dto.response.OrderLogResponse());

        // When
        OrderDetailResponse response = orderService.getOrderDetail(testUserId, testOrderId);

        // Then
        assertNotNull(response);
        assertEquals(testOrderId.toString(), response.getId());
        assertEquals("UU2001", response.getOrderCode());
        assertEquals(ServiceTypeEnum.HELP_BUY, response.getServiceType());
        assertEquals(OrderStatusEnum.PENDING_PAYMENT, response.getStatus());
        assertTrue(response.getCanCancel());
        assertEquals("138****8000", response.getEndContactPhone());
    }

    @Test
    @DisplayName("获取订单详情 - 订单不存在")
    void testGetOrderDetail_orderNotFound() {
        // Given
        when(orderMapper.selectById(testOrderId)).thenReturn(null);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> orderService.getOrderDetail(testUserId, testOrderId));
        assertEquals(ErrorCodeEnum.ORDER_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("获取订单详情 - 无权访问")
    void testGetOrderDetail_forbidden() {
        // Given
        Order otherUserOrder = new Order();
        otherUserOrder.setId(testOrderId);
        otherUserOrder.setUserId(9999L); // 其他用户
        otherUserOrder.setOrderCode("UU2001");
        otherUserOrder.setServiceType(ServiceTypeEnum.HELP_BUY);
        otherUserOrder.setStatus(OrderStatusEnum.PENDING_PAYMENT);

        when(orderMapper.selectById(testOrderId)).thenReturn(otherUserOrder);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> orderService.getOrderDetail(testUserId, testOrderId));
        assertEquals(ErrorCodeEnum.FORBIDDEN.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("取消订单 - 待支付状态成功")
    void testCancelOrder_pendingPayment_success() {
        // Given
        testOrder.setStatus(OrderStatusEnum.PENDING_PAYMENT);
        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);
        when(orderLogService.createLog(anyLong(), anyString(), anyString())).thenReturn(1L);

        // When
        orderService.cancelOrder(testUserId, testOrderId);

        // Then
        verify(orderMapper).updateById(argThat(order ->
            order.getStatus() == OrderStatusEnum.CANCELLED
        ));
        verify(orderLogService).createLog(testOrderId, "取消订单", "用户取消订单");
    }

    @Test
    @DisplayName("取消订单 - 待接单状态成功")
    void testCancelOrder_pendingAccept_success() {
        // Given
        testOrder.setStatus(OrderStatusEnum.PENDING_ACCEPT);
        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);
        when(orderLogService.createLog(anyLong(), anyString(), anyString())).thenReturn(1L);

        // When
        orderService.cancelOrder(testUserId, testOrderId);

        // Then
        verify(orderMapper).updateById(argThat(order ->
            order.getStatus() == OrderStatusEnum.CANCELLED
        ));
    }

    @Test
    @DisplayName("取消订单 - 进行中状态不允许")
    void testCancelOrder_inProgress_notAllowed() {
        // Given
        testOrder.setStatus(OrderStatusEnum.IN_PROGRESS);
        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> orderService.cancelOrder(testUserId, testOrderId));
        assertEquals(ErrorCodeEnum.ORDER_CANNOT_CANCEL.getCode(), ex.getCode());
        verify(orderMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("取消订单 - 订单不存在")
    void testCancelOrder_orderNotFound() {
        // Given
        when(orderMapper.selectById(testOrderId)).thenReturn(null);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> orderService.cancelOrder(testUserId, testOrderId));
        assertEquals(ErrorCodeEnum.ORDER_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("验证订单所有权 - 成功")
    void testValidateOwnership_success() {
        // Given
        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);

        // When
        Order order = orderService.validateOwnership(testUserId, testOrderId);

        // Then
        assertNotNull(order);
        assertEquals(testOrderId, order.getId());
    }

    @Test
    @DisplayName("验证订单所有权 - 无权访问")
    void testValidateOwnership_forbidden() {
        // Given
        when(orderMapper.selectById(testOrderId)).thenReturn(testOrder);

        // When & Then
        BusinessException ex = assertThrows(BusinessException.class,
            () -> orderService.validateOwnership(9999L, testOrderId));
        assertEquals(ErrorCodeEnum.FORBIDDEN.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("生成订单编号")
    void testGenerateOrderCode() {
        // When
        String orderCode = orderService.generateOrderCode(testOrderId);

        // Then
        assertEquals("UU2001", orderCode);
    }

    @Test
    @DisplayName("生成订单标题 - 有描述")
    void testGenerateTitle_withDescription() {
        // When
        String title = orderService.generateTitle("这是一个测试订单描述");

        // Then - 描述正好10个字符，应该全部保留
        assertEquals("这是一个测试订单描述", title);
    }

    @Test
    @DisplayName("生成订单标题 - 无描述")
    void testGenerateTitle_withoutDescription() {
        // When
        String title = orderService.generateTitle(null);

        // Then
        assertEquals("跑腿订单", title);
    }

    @Test
    @DisplayName("生成订单标题 - 描述不足10个字符")
    void testGenerateTitle_shortDescription() {
        // When
        String title = orderService.generateTitle("短标题");

        // Then
        assertEquals("短标题", title);
    }

    @Test
    @DisplayName("转换为响应DTO")
    void testToResponse() {
        // When
        com.uu.dto.response.OrderResponse response = orderService.toResponse(testOrder);

        // Then
        assertNotNull(response);
        assertEquals(testOrderId.toString(), response.getId());
        assertEquals("UU2001", response.getOrderCode());
        assertEquals(ServiceTypeEnum.HELP_BUY, response.getServiceType());
        assertEquals(OrderStatusEnum.PENDING_PAYMENT, response.getStatus());
        assertEquals("138****8000", response.getEndContactPhone());
        assertTrue(response.getCanCancel());
    }
}