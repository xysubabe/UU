package com.uu.controller.auth;

import com.uu.dto.request.WechatLoginRequest;
import com.uu.dto.response.ApiResponse;
import com.uu.dto.response.LoginResponse;
import com.uu.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 微信登录
     */
    @PostMapping("/wechat-login")
    public ApiResponse<LoginResponse> wechatLogin(@Valid @RequestBody WechatLoginRequest request) {
        log.info("微信登录请求, code={}", request.getCode());
        LoginResponse response = userService.wechatLogin(request);
        return ApiResponse.success(response);
    }
}