package com.uu.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 订单日志列表响应
 */
@Data
public class OrderLogListResponse {

    /**
     * 日志列表
     */
    @JsonProperty("list")
    private List<OrderLogResponse> list;
}