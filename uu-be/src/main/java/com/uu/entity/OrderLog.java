package com.uu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单日志表实体
 */
@Data
@TableName("t_order_log")
public class OrderLog {

    /**
     * 日志ID（雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 操作描述
     */
    private String description;

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