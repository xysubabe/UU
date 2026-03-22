package com.uu.controller.user;

import com.uu.dto.response.ApiResponse;
import com.uu.dto.response.UserResponse;
import com.uu.interceptor.LoginInterceptor;
import com.uu.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public ApiResponse<UserResponse> getUserInfo(HttpServletRequest request) {
        Long userId = LoginInterceptor.getUserId(request);
        log.info("获取用户信息, userId={}", userId);
        UserResponse response = userService.getUserInfo(userId);
        return ApiResponse.success(response);
    }
}