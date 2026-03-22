package com.uu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地址响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    /**
     * 地址ID
     */
    private String id;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话（脱敏）
     */
    private String contactPhone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区/县
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 完整地址
     */
    private String fullAddress;

    /**
     * 是否默认地址：1是 0否
     */
    private Integer isDefault;
}