package com.uu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求DTO
 */
@Data
public class WechatLoginRequest {

    /**
     * 微信登录code
     */
    @NotBlank(message = "code不能为空")
    private String code;
}