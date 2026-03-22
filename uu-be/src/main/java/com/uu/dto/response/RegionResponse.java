package com.uu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 省市区响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionResponse {

    /**
     * 地区ID
     */
    private Long id;

    /**
     * 地区编码
     */
    private String code;

    /**
     * 地区名称
     */
    private String name;

    /**
     * 层级：1省 2市 3区县
     */
    private Integer level;

    /**
     * 父级地区编码
     */
    private String parentCode;
}