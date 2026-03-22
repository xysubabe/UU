package com.uu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 订单列表响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponse {

    /**
     * 订单列表
     */
    private List<OrderResponse> list;

    /**
     * 总数
     */
    private Integer total;

    /**
     * 当前页
     */
    private Integer page;

    /**
     * 每页数量
     */
    private Integer pageSize;
}