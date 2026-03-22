package com.uu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.uu.constants.OrderConstants;
import com.uu.constants.PaymentConstants;
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
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 * <p>
 * 提供订单的创建、查询、取消等核心业务功能。
 * 包含订单状态管理、所有权验证、地址信息处理等逻辑。
 * </p>
 *
 * @author UU Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

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
            log.warn("创建订单失败，无效的服务类型, userId={}, serviceType={}", userId, request.getServiceType());
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
                    log.warn("创建订单失败，帮送订单缺少起止地址, userId={}, startAddressId={}, endAddressId={}",
                            userId, request.getStartAddressId(), request.getEndAddressId());
                    throw new BusinessException(ErrorCodeEnum.INVALID_PARAMS);
                }
                // 验证起点和终点不能相同
                if (request.getStartAddressId().equals(request.getEndAddressId())) {
                    log.warn("创建订单失败，帮送订单起止地址相同, userId={}, addressId={}", userId, request.getStartAddressId());
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
        // 金额单位：分转换为元
        BigDecimal amount = new BigDecimal(request.getAmount())
                .divide(new BigDecimal(PaymentConstants.AMOUNT_DIVISOR), PaymentConstants.AMOUNT_SCALE, PaymentConstants.AMOUNT_ROUNDING_MODE);
        order.setAmount(amount);
        order.setDeliveryFee(amount);

        // 设置地址信息
        if (addressId != null) {
            Address address = addressService.getById(addressId);
            if (address == null) {
                log.warn("创建订单失败，地址不存在, userId={}, addressId={}", userId, addressId);
                throw new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND);
            }
            String fullAddress = address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress();

            switch (serviceType) {
                case HELP_BUY:
                    order.setEndAddress(fullAddress);
                    order.setEndContactName(address.getContactName());
                    order.setEndContactPhone(address.getContactPhone());
                    break;
                case HELP_SEND:
                    Address startAddress = addressService.getById(request.getStartAddressId());
                    if (startAddress == null) {
                        log.warn("创建订单失败，起点地址不存在, userId={}, startAddressId={}", userId, request.getStartAddressId());
                        throw new BusinessException(ErrorCodeEnum.ADDRESS_NOT_FOUND);
                    }
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
        // 参数验证
        if (page == null || page < 1) {
            page = 1;
        }
        if (pageSize == null || pageSize < 1 || pageSize > OrderConstants.MAX_PAGE_SIZE) {
            pageSize = OrderConstants.DEFAULT_PAGE_SIZE;
        }

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
    public OrderListResponse getCompletedOrders(Long userId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId)
                .eq(Order::getStatus, OrderStatusEnum.COMPLETED)
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

        log.info("获取已完成订单, userId={}, total={}", userId, pageResult.getTotal());
        return response;
    }

    @Override
    public OrderStatsResponse getOrderStats(Long userId) {
        // 查询总订单数
        LambdaQueryWrapper<Order> totalQuery = new LambdaQueryWrapper<>();
        totalQuery.eq(Order::getUserId, userId);
        Long totalOrders = orderMapper.selectCount(totalQuery);

        // 查询已完成订单数
        LambdaQueryWrapper<Order> completedQuery = new LambdaQueryWrapper<>();
        completedQuery.eq(Order::getUserId, userId)
                .eq(Order::getStatus, OrderStatusEnum.COMPLETED);
        Long completedOrders = orderMapper.selectCount(completedQuery);

        // 查询进行中订单数
        LambdaQueryWrapper<Order> ongoingQuery = new LambdaQueryWrapper<>();
        ongoingQuery.eq(Order::getUserId, userId)
                .in(Order::getStatus, OrderStatusEnum.PENDING_PAYMENT, OrderStatusEnum.PENDING_ACCEPT, OrderStatusEnum.IN_PROGRESS);
        Long ongoingOrders = orderMapper.selectCount(ongoingQuery);

        log.info("获取订单统计, userId={}, total={}, completed={}, ongoing={}",
                userId, totalOrders, completedOrders, ongoingOrders);

        return OrderStatsResponse.of(totalOrders, completedOrders, ongoingOrders);
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
            log.warn("订单不存在, userId={}, orderId={}", userId, orderId);
            throw new BusinessException(ErrorCodeEnum.ORDER_NOT_FOUND);
        }

        if (!order.getUserId().equals(userId)) {
            log.warn("用户无权访问订单, userId={}, orderId={}, orderUserId={}", userId, orderId, order.getUserId());
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

               int length = Math.min(description.length(), OrderConstants.MAX_TITLE_LENGTH);
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
        if (!StringUtils.hasText(phone) || phone.length() != OrderConstants.PHONE_LENGTH) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}