package com.uu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 地址表实体
 */
@Data
@TableName("t_address")
public class Address {

    /**
     * 地址ID（雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
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
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 区/县编码
     */
    private String districtCode;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 是否默认地址：1是 0否
     */
    private Integer isDefault;

    /**
     * 状态：1启用 0删除
     */
    private Integer status;

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