package com.uu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
     * 起点地址（帮送必填）
     */
    private String startAddress;

    /**
     * 终点地址ID（帮送必填）
     */
    private Long endAddressId;

    /**
     * 终点地址（帮送必填）
     */
    private String endAddress;

    /**
     * 终点联系人（帮送必填）
     */
    private String endContactName;

    /**
     * 终点联系人电话（帮送必填）
     */
    private String endContactPhone;

    /**
     * 订单描述（最大200字符，非必填）
     */
    private String description;

    /**
     * 跑腿费用
     */
    @NotNull(message = "跑腿费用不能为空")
    private Integer amount;
}