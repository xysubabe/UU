package com.uu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户信息
     */
    private UserInfoResponse userInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoResponse {
        private String id;
        private String nickname;
        private String avatarUrl;
    }
}