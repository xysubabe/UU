package com.uu.dto.response;

import com.uu.enums.OrderStatusEnum;
import com.uu.enums.ServiceTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {

    /**
     * 订单ID
     */
    private String id;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 服务类型
     */
    private ServiceTypeEnum serviceType;

    /**
     * 服务类型描述
     */
    private String serviceTypeDesc;

    /**
     * 订单状态
     */
    private OrderStatusEnum status;

    /**
     * 订单状态描述
     */
    private String statusDesc;

    /**
     * 订单标题
     */
    private String title;

    /**
     * 订单描述
     */
    private String description;

    /**
     * 起点地址
     */
    private String startAddress;

    /**
     * 终点地址
     */
    private String endAddress;

    /**
     * 终点联系人姓名
     */
    private String endContactName;

    /**
     * 终点联系人电话（脱敏）
     */
    private String endContactPhone;

    /**
     * 订单总金额
     */
    private BigDecimal amount;

    /**
     * 跑腿费
     */
    private BigDecimal deliveryFee;

    /**
     * 是否可以取消
     */
    private Boolean canCancel;

    /**
     * 订单日志
     */
    private List<OrderLogResponse> orderLogs;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    private LocalDateTime updateAt;
}