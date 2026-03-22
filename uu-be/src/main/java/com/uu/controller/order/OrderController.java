package com.uu.controller.order;

import com.uu.dto.request.OrderCreateRequest;
import com.uu.dto.response.ApiResponse;
import com.uu.dto.response.IdStringResponse;
import com.uu.dto.response.OrderDetailResponse;
import com.uu.dto.response.OrderListResponse;
import com.uu.interceptor.LoginInterceptor;
import com.uu.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public ApiResponse<IdStringResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            HttpServletRequest httpRequest) {
        Long userId = LoginInterceptor.getUserId(httpRequest);
        log.info("创建订单, userId={}, serviceType={}", userId, request.getServiceType());
        Long orderId = orderService.createOrder(userId, request);
        return ApiResponse.success(IdStringResponse.of(orderId));
    }

    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    public ApiResponse<OrderListResponse> getOrderList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = LoginInterceptor.getUserId(request);
        log.info("获取订单列表, userId={}, status={}", userId, status);
        OrderListResponse response = orderService.getOrderList(userId, status, page, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 获取进行中订单（首页）
     */
    @GetMapping("/ongoing")
    public ApiResponse<OrderListResponse> getOngoingOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long userId = LoginInterceptor.getUserId(request);
        log.info("获取进行中订单, userId={}", userId);
        OrderListResponse response = orderService.getOngoingOrders(userId, page, pageSize);
        return ApiResponse.success(response);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrderDetail(
            @PathVariable Long orderId,
            HttpServletRequest request) {
        Long userId = LoginInterceptor.getUserId(request);
        log.info("获取订单详情, userId={}, orderId={}", userId, orderId);
        OrderDetailResponse response = orderService.getOrderDetail(userId, orderId);
        return ApiResponse.success(response);
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{orderId}")
    public ApiResponse<Void> cancelOrder(
            @PathVariable Long orderId,
            HttpServletRequest request) {
        Long userId = LoginInterceptor.getUserId(request);
        log.info("取消订单, userId={}, orderId={}", userId, orderId);
        orderService.cancelOrder(userId, orderId);
        return ApiResponse.success(null);
    }
}