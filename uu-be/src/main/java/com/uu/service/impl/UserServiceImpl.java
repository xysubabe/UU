package com.uu.service.impl;

import com.uu.dto.request.WechatLoginRequest;
import com.uu.dto.response.LoginResponse;
import com.uu.dto.response.UserResponse;
import com.uu.entity.User;
import com.uu.enums.ErrorCodeEnum;
import com.uu.exception.BusinessException;
import com.uu.mapper.UserMapper;
import com.uu.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现（测试模式）
 */
@Slf4j
@Service
public class UserServiceImpl implements com.uu.service.UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse wechatLogin(WechatLoginRequest request) {
        log.warn("使用测试模式登录（WeChat SDK 已禁用）");

        // 测试模式：使用固定的openid用于本地测试
        String openid = "test-openid-" + System.currentTimeMillis();
        String unionid = "test-unionid-" + System.currentTimeMillis();

        // 查询用户是否存在
        User user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getOpenid, openid)
        );

        // 用户不存在则创建
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setStatus(1);
            userMapper.insert(user);
            log.info("创建新用户, userId={}", user.getId());
        } else {
            // 更新unionid
            if (unionid != null && !unionid.equals(user.getUnionid())) {
                user.setUnionid(unionid);
                userMapper.updateById(user);
            }
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCodeEnum.USER_DISABLED);
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getNickname());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);

        LoginResponse.UserInfoResponse userInfo = new LoginResponse.UserInfoResponse();
        userInfo.setId(user.getId().toString());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        response.setUserInfo(userInfo);

        log.info("用户登录成功, userId={}", user.getId());
        return response;
    }

    @Override
    public User getById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public UserResponse getUserInfo(Long userId) {
        User user = getById(userId);

        UserResponse response = new UserResponse();
        response.setId(user.getId().toString());
        response.setNickname(user.getNickname());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setPhone(user.getPhone());
        response.setCreateAt(user.getCreateAt());

        return response;
    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        User user = getById(userId);
        user.setAvatarUrl(avatarUrl);
        userMapper.updateById(user);
        log.info("更新用户头像, userId={}", userId);
    }
}