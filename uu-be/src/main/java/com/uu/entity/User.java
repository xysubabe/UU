package com.uu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户表实体
 */
@Data
@TableName("t_user")
public class User {

    /**
     * 用户ID（雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 微信unionid
     */
    private String unionid;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL（存储相对路径）
     */
    private String avatarUrl;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态：1正常 0禁用
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