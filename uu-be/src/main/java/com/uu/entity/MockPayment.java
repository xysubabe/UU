package com.uu.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.uu.enums.PaymentStatusEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mock支付表实体
 */
@Data
@TableName("t_mock_payment")
public class MockPayment {

    /**
     * Mock支付ID（雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * Mock支付金额
     */
    private BigDecimal amount;

    /**
     * 状态：0待支付 1已支付
     */
    private PaymentStatusEnum status;

    /**
     * Mock结果（JSON格式）
     */
    private String mockResult;

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