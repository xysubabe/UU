package com.uu.service;

import com.uu.dto.request.WechatLoginRequest;
import com.uu.dto.response.LoginResponse;
import com.uu.dto.response.UserResponse;
import com.uu.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 微信登录
     * @param request 微信登录请求
     * @return 登录响应
     */
    LoginResponse wechatLogin(WechatLoginRequest request);

    /**
     * 根据ID获取用户
     * @param userId 用户ID
     * @return 用户实体
     */
    User getById(Long userId);

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户响应
     */
    UserResponse getUserInfo(Long userId);

    /**
     * 更新用户头像URL
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     */
    void updateAvatar(Long userId, String avatarUrl);
}