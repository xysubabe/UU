package com.uu.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建订单请求DTO
 */
@Data
public class OrderCreateRequest {

    /**
     * 服务类型：1帮买 2帮送 3帮排队
     */
    @NotNull(message = "服务类型不能为空")
    private Integer serviceType;

    /**
     * 起点地址ID（帮送必填）
     */
    private Long startAddressId;

    /**
     * 终点地址ID（帮买、帮送必填）
     */
    private Long endAddressId;

    /**
     * 排队地址ID（帮排队必填）
     */
    private Long queueAddressId;

    /**
     * 订单描述（最大200字符，非必填）
     */
    @Size(max = 200, message = "订单描述不能超过200个字符")
    private String description;

    /**
     * 跑腿费用（单位：分）
     */
    @NotNull(message = "跑腿费用不能为空")
    private Integer amount;
}