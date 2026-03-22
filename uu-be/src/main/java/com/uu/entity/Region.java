package com.uu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 省市区表实体
 */
@Data
@TableName("t_region")
public class Region {

    /**
     * 地区ID（数据库自增）
     */
    @TableId(type = IdType.AUTO)
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