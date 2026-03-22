package com.uu.service;

import com.uu.entity.OrderLog;

import java.util.List;

/**
 * 订单日志服务接口
 */
public interface OrderLogService {

    /**
     * 创建订单日志
     * @param orderId 订单ID
     * @param action 操作类型
     * @param description 操作描述
     * @return 日志ID
     */
    Long createLog(Long orderId, String action, String description);

    /**
     * 获取订单日志列表
     * @param orderId 订单ID
     * @return 日志列表
     */
    List<OrderLog> getOrderLogs(Long orderId);

    /**
     * 转换为响应DTO
     * @param log 日志实体
     * @return 响应DTO
     */
    com.uu.dto.response.OrderLogResponse toResponse(OrderLog log);
}