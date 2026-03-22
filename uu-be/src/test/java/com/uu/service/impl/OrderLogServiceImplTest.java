package com.uu.service.impl;

import com.uu.entity.OrderLog;
import com.uu.enums.ErrorCodeEnum;
import com.uu.exception.BusinessException;
import com.uu.mapper.OrderLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 订单日志服务测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("OrderLogService 测试")
class OrderLogServiceImplTest {

    @Mock
    private OrderLogMapper orderLogMapper;

    @InjectMocks
    private OrderLogServiceImpl orderLogService;

    private Long testOrderId;
    private OrderLog testOrderLog;

    @BeforeEach
    void setUp() {
        testOrderId = 2001L;

        testOrderLog = new OrderLog();
        testOrderLog.setId(3001L);
        testOrderLog.setOrderId(testOrderId);
        testOrderLog.setAction("创建订单");
        testOrderLog.setDescription("订单创建成功");
    }

    @Test
    @DisplayName("创建订单日志 - 成功")
    void testCreateLog_success() {
        // Given
        when(orderLogMapper.insert(any(OrderLog.class))).thenAnswer(invocation -> {
            OrderLog log = invocation.getArgument(0);
            log.setId(3001L);
            return 1;
        });

        // When
        Long logId = orderLogService.createLog(testOrderId, "取消订单", "用户取消订单");

        // Then
        assertNotNull(logId);
        assertEquals(3001L, logId);
        verify(orderLogMapper).insert(argThat(log ->
            log.getOrderId().equals(testOrderId) &&
            log.getAction().equals("取消订单") &&
            log.getDescription().equals("用户取消订单")
        ));
    }

    @Test
    @DisplayName("获取订单日志 - 成功")
    void testGetOrderLogs_success() {
        // Given
        when(orderLogMapper.selectList(any())).thenReturn(List.of(testOrderLog));

        // When
        List<OrderLog> logs = orderLogService.getOrderLogs(testOrderId);

        // Then
        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals(testOrderId, logs.get(0).getOrderId());
    }

    @Test
    @DisplayName("转换为响应DTO")
    void testToResponse() {
        // When
        com.uu.dto.response.OrderLogResponse response = orderLogService.toResponse(testOrderLog);

        // Then
        assertNotNull(response);
        assertEquals("3001", response.getId());
        assertEquals("创建订单", response.getAction());
        assertEquals("订单创建成功", response.getDescription());
    }
}