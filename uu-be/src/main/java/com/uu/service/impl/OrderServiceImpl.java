package com.uu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uu.dto.request.OrderCreateRequest;
import com.uu.dto.response.*;
import com.uu.entity.Address;
import com.uu.entity.Order;
import com.uu.entity.OrderLog;
import com.uu.enums.ErrorCodeEnum;
import com.uu.enums.OrderStatusEnum;
import com.uu.enums.ServiceTypeEnum;
import com.uu.exception.BusinessException;
import com.uu.mapper.OrderMapper;
import com.uu.service.AddressService;
import com.uu.service.OrderLogService;
import com.uu.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private static final int MAX_TITLE_LENGTH = 10;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private AddressService addressService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, OrderCreateRequest request) {
        ServiceTypeEnum serviceType = ServiceTypeEnum.getByCode(request.getServiceType());
        if (serviceType == null) {
            throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS);
        }

        // 验证地址ID
        Long addressId = null;
        switch (serviceType) {
            case HELP_BUY:
                addressId = request.getEndAddressId();
                break;
            case HELP_SEND:
                // 帮送需要起点和终点地址
                if (request.getStartAddressId() == null || request.getEndAddressId() == null) {
                    throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS);
                }
                addressId = request.getEndAddressId();
                break;
            case HELP_QUEUE:
                addressId = request.getQueueAddressId();
                break;
        }

        // 验证地址所有权
        if (addressId != null) {
            addressService.validateOwnership(userId, addressId);
        }

        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setServiceType(serviceType);
        order.setStatus(OrderStatusEnum.PENDING_PAYMENT);
        order.setDescription(request.getDescription());
        order.setTitle(generateTitle(request.getDescription()));
        order.setAmount(new BigDecimal(request.getAmount()));
        order.setDeliveryFee(new BigDecimal(request.getAmount()));

        // 设置地址信息
        if (addressId != null) {
            Address address = addressService.getById(addressId);
            String fullAddress = address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress();

            switch (serviceType) {
                case HELP_BUY:
                    order.setEndAddress(fullAddress);
                    order.setEndContactName(address.getContactName());
                    order.setEndContactPhone(address.getContactPhone());
                    break;
                case HELP_SEND:
                    Address startAddress = addressService.getById(request.getStartAddressId());
                    String startFullAddress = startAddress.getProvince() + startAddress.getCity() + startAddress.getDistrict() + startAddress.getDetailAddress();
                    order.setStartAddress(startFullAddress);
                    order.setEndAddress(fullAddress);
                    order.setEndContactName(address.getContactName());
                    order.setEndContactPhone(address.getContactPhone());
                    break;
                case HELP_QUEUE:
                    order.setEndAddress(fullAddress);
                    order.setEndContactName(address.getContactName());
                    order.setEndContactPhone(address.getContactPhone());
                    break;
            }
        }

        orderMapper.insert(order);

        // 生成订单编号
        order.setOrderCode(generateOrderCode(order.getId()));
        orderMapper.updateById(order);

        // 创建订单日志
        orderLogService.createLog(order.getId(), "创建订单", "订单创建成功");

        log.info("创建订单成功, userId={}, orderId={}, orderCode={}", userId, order.getId(), order.getOrderCode());
        return order.getId();
    }

    @Override
    public OrderListResponse getOrderList(Long userId, Integer status, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);

        // 状态筛选
        if (status != null) {
            OrderStatusEnum orderStatus = OrderStatusEnum.getByCode(status);
            if (orderStatus != null) {
                queryWrapper.eq(Order::getStatus, orderStatus);
            }
        }

        queryWrapper.orderByDesc(Order::getCreateAt);

        // 分页查询
        Page<Order> pageResult = orderMapper.selectPage(
                new Page<>(page, pageSize),
                queryWrapper
        );

        // 转换为响应DTO
        List<OrderResponse> responseList = pageResult.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        OrderListResponse response = new OrderListResponse();
        response.setList(responseList);
        response.setTotal((int) pageResult.getTotal());
        response.setPage((int) pageResult.getCurrent());
        response.setPageSize((int) pageResult.getSize());

        log.info("获取订单列表, userId={}, status={}, total={}", userId, status, pageResult.getTotal());
        return response;
    }

    @Override
    public OrderListResponse getOngoingOrders(Long userId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                .in(Order::getStatus, OrderStatusEnum.PENDING_PAYMENT, OrderStatusEnum.PENDING_ACCEPT, OrderStatusEnum.IN_PROGRESS)
                .orderByDesc(Order::getCreateAt);

        // 分页查询
        Page<Order> pageResult = orderMapper.selectPage(
                new Page<>(page, pageSize),
                queryWrapper
        );

        // 转换为响应DTO
        List<OrderResponse> responseList = pageResult.getRecords().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        OrderListResponse response = new OrderListResponse();
        response.setList(responseList);
        response.setTotal((int) pageResult.getTotal());
        response.setPage((int) pageResult.getCurrent());
        response.setPageSize((int) pageResult.getSize());

        log.info("获取进行中订单, userId={}, total={}", userId, pageResult.getTotal());
        return response;
    }

    @Override
    public OrderDetailResponse getOrderDetail(Long userId, Long orderId) {
        // 验证订单所有权
        Order order = validateOwnership(userId, orderId);

        // 获取订单日志
        List<OrderLog> orderLogs = orderLogService.getOrderLogs(orderId);

        // 构建响应
        OrderDetailResponse response = new OrderDetailResponse();
        BeanUtils.copyProperties(order, response);
        response.setId(order.getId().toString());
        response.setServiceTypeDesc(order.getServiceType().getDesc());
        response.setStatusDesc(order.getStatus().getDesc());
        response.setCanCancel(order.getStatus().canCancel());
        response.setEndContactPhone(maskPhone(order.getEndContactPhone()));

        // 订单日志
        List<OrderLogResponse> logResponses = orderLogs.stream()
                .map(orderLogService::toResponse)
                .collect(Collectors.toList());
        response.setOrderLogs(logResponses);

        log.info("获取订单详情, userId={}, orderId={}", userId, orderId);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        Order order = validateOwnership(userId, orderId);

        // 验证订单状态
        if (!order.getStatus().canCancel()) {
            throw new BusinessException(ErrorCodeEnum.ORDER_CANNOT_CANCEL);
        }

        // 更新订单状态
        order.setStatus(OrderStatusEnum.CANCELLED);
        orderMapper.updateById(order);

        // 创建订单日志
        orderLogService.createLog(orderId, "取消订单", "用户取消订单");

        log.info("取消订单成功, userId={}, orderId={}", userId, orderId);
    }

    @Override
    public Order validateOwnership(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND);
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN);
        }

        return order;
    }

    @Override
    public String generateOrderCode(Long orderId) {
        return "UU" + orderId;
    }

    @Override
    public String generateTitle(String description) {
        if (!StringUtils.hasText(description)) {
            return "跑腿订单";
        }

        int length = Math.min(description.length(), MAX_TITLE_LENGTH);
        return description.substring(0, length);
    }

    @Override
    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        response.setId(order.getId().toString());
        response.setServiceTypeDesc(order.getServiceType().getDesc());
        response.setStatusDesc(order.getStatus().getDesc());
        response.setCanCancel(order.getStatus().canCancel());
        response.setEndContactPhone(maskPhone(order.getEndContactPhone()));
        return response;
    }

    /**
     * 手机号脱敏
     */
    private String maskPhone(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}