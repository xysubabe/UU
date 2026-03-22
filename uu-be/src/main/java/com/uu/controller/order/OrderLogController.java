package com.uu.controller.order;

import com.uu.dto.response.ApiResponse;
import com.uu.dto.response.OrderLogListResponse;
import com.uu.interceptor.LoginInterceptor;
import com.uu.service.OrderLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单日志控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderLogController {

    @Autowired
    private OrderLogService orderLogService;

    /**
     * 获取订单日志列表
     */
    @GetMapping("/{orderId}/logs")
    public ApiResponse<OrderLogListResponse> getOrderLogs(
            @PathVariable Long orderId,
            HttpServletRequest request) {
        Long userId = LoginInterceptor.getUserId(request);
        log.info("获取订单日志, userId={}, orderId={}", userId, orderId);
        OrderLogListResponse response = orderLogService.getOrderLogList(orderId);
        return ApiResponse.success(response);
    }
}