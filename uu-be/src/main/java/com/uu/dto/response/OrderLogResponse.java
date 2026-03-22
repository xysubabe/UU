package com.uu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 订单日志响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLogResponse {

    /**
     * 日志ID
     */
    private String id;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;
}