package com.uu.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 订单统计响应
 */
@Data
public class OrderStatsResponse {

    /**
     * 总订单数
     */
    @JsonProperty("totalOrders")
    private Long totalOrders;

    /**
     * 已完成订单数
     */
    @JsonProperty("completedOrders")
    private Long completedOrders;

    /**
     * 进行中订单数
     */
    @JsonProperty("ongoingOrders")
    private Long ongoingOrders;

    /**
     * 创建响应
     */
    public static OrderStatsResponse of(Long totalOrders, Long completedOrders, Long ongoingOrders) {
        OrderStatsResponse response = new OrderStatsResponse();
        response.setTotalOrders(totalOrders);
        response.setCompletedOrders(completedOrders);
        response.setOngoingOrders(ongoingOrders);
        return response;
    }
}