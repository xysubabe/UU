package com.uu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.uu.dto.response.OrderLogListResponse;
import com.uu.dto.response.OrderLogResponse;
import com.uu.entity.OrderLog;
import com.uu.mapper.OrderLogMapper;
import com.uu.service.OrderLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单日志服务实现
 */
@Slf4j
@Service
public class OrderLogServiceImpl implements OrderLogService {

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Override
    public Long createLog(Long orderId, String action, String description) {
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderId);
        orderLog.setAction(action);
        orderLog.setDescription(description);

        orderLogMapper.insert(orderLog);
        log.info("创建订单日志, orderId={}, action={}, logId={}", orderId, action, orderLog.getId());
        return orderLog.getId();
    }

    @Override
    public List<OrderLog> getOrderLogs(Long orderId) {
        LambdaQueryWrapper<OrderLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLog::getOrderId, orderId)
                .orderByDesc(OrderLog::getCreateAt);

        List<OrderLog> logs = orderLogMapper.selectList(queryWrapper);
        log.info("获取订单日志, orderId={}, count={}", orderId, logs.size());
        return logs;
    }

    @Override
    public OrderLogListResponse getOrderLogList(Long orderId) {
        List<OrderLog> logs = getOrderLogs(orderId);
        List<OrderLogResponse> responseList = logs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        OrderLogListResponse response = new OrderLogListResponse();
        response.setList(responseList);
        return response;
    }

    @Override
    public OrderLogResponse toResponse(OrderLog log) {
        OrderLogResponse response = new OrderLogResponse();
        response.setId(log.getId().toString());
        response.setAction(log.getAction());
        response.setDescription(log.getDescription());
        response.setCreateAt(log.getCreateAt());
        return response;
    }
}