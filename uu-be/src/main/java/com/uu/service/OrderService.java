package com.uu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uu.dto.request.OrderCreateRequest;
import com.uu.dto.response.OrderDetailResponse;
import com.uu.dto.response.OrderListResponse;
import com.uu.dto.response.OrderResponse;
import com.uu.dto.response.OrderStatsResponse;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     * @param userId 用户ID
     * @param request 创建请求
     * @return 订单ID
     */
    Long createOrder(Long userId, OrderCreateRequest request);

    /**
     * 获取订单列表
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @param page 页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    OrderListResponse getOrderList(Long userId, Integer status, Integer page, Integer pageSize);

    /**
     * 获取进行中订单
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    OrderListResponse getOngoingOrders(Long userId, Integer page, Integer pageSize);

    /**
     * 获取已完成订单
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    OrderListResponse getCompletedOrders(Long userId, Integer page, Integer pageSize);

    /**
     * 获取订单统计
     * @param userId 用户ID
     * @return 订单统计
     */
    OrderStatsResponse getOrderStats(Long userId);

    /**
     * 获取订单详情
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDetailResponse getOrderDetail(Long userId, Long orderId);

    /**
     * 取消订单
     * @param userId 用户ID
     * @param orderId 订单ID
     */
    void cancelOrder(Long userId, Long orderId);

    /**
     * 验证订单所有权
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 订单实体
     */
    com.uu.entity.Order validateOwnership(Long userId, Long orderId);

    /**
     * 生成订单编号
     * @param orderId 订单ID
     * @return 订单编号
     */
    String generateOrderCode(Long orderId);

    /**
     * 生成订单标题
     * @param description 订单描述
     * @return 订单标题
     */
    String generateTitle(String description);

    /**
     * 转换为响应DTO
     * @param order 订单实体
     * @return 响应DTO
     */
    OrderResponse toResponse(com.uu.entity.Order order);
}