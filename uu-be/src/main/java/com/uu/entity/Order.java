package com.uu.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.uu.enums.OrderStatusEnum;
import com.uu.enums.ServiceTypeEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表实体
 */
@Data
@TableName("t_order")
public class Order {

    /**
     * 订单ID（雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单编号（格式：UU + 订单ID）
     */
    private String orderCode;

    /**
     * 服务类型：1帮买 2帮送 3帮排队
     */
    private ServiceTypeEnum serviceType;

    /**
     * 订单状态：100待支付 200待接单 300进行中 600已完成 999已取消
     */
    private OrderStatusEnum status;

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
     * 终点联系人电话
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
     * 支付ID
     */
    private Long paymentId;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer schemaVersion;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt;
}