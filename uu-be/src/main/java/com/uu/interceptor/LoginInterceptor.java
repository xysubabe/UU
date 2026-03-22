package com.uu.interceptor;

import com.uu.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录校验拦截器
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_ATTRIBUTE = "userId";

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取Authorization请求头
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":1002,\"message\":\"未授权，请先登录\",\"data\":null,\"timestamp\":" + System.currentTimeMillis() + "}");
            } catch (Exception e) {
                log.error("写入响应失败", e);
            }
            return false;
        }

        // 提取Token
        String token = authHeader.substring(BEARER_PREFIX.length());

        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":1002,\"message\":\"Token无效或已过期\",\"data\":null,\"timestamp\":" + System.currentTimeMillis() + "}");
            } catch (Exception e) {
                log.error("写入响应失败", e);
            }
            return false;
        }

        // 从Token中获取用户ID并存入请求属性
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            request.setAttribute(USER_ID_ATTRIBUTE, userId);
        } catch (Exception e) {
            log.error("解析Token失败", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":1002,\"message\":\"Token解析失败\",\"data\":null,\"timestamp\":" + System.currentTimeMillis() + "}");
            } catch (Exception ex) {
                log.error("写入响应失败", ex);
            }
            return false;
        }

        return true;
    }

    /**
     * 从请求中获取用户ID
     */
    public static Long getUserId(HttpServletRequest request) {
        Object userId = request.getAttribute(USER_ID_ATTRIBUTE);
        return userId != null ? (Long) userId : null;
    }
}