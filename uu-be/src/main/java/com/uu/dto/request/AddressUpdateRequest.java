package com.uu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 更新地址请求DTO
 */
@Data
public class AddressUpdateRequest {

    /**
     * 地址ID
     */
    @NotNull(message = "地址ID不能为空")
    private Long id;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    /**
     * 联系人电话
     */
    @NotBlank(message = "联系人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;

    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    private String province;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;

    /**
     * 区/县
     */
    @NotBlank(message = "区/县不能为空")
    private String district;

    /**
     * 省份编码
     */
    @NotBlank(message = "省份编码不能为空")
    private String provinceCode;

    /**
     * 城市编码
     */
    @NotBlank(message = "城市编码不能为空")
    private String cityCode;

    /**
     * 区/县编码
     */
    @NotBlank(message = "区/县编码不能为空")
    private String districtCode;

    /**
     * 详细地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    /**
     * 是否默认地址：1是 0否
     */
    private Integer isDefault;
}