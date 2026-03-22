package com.uu.interceptor;

import com.uu.annotation.DevOpsAuth;
import com.uu.exception.BusinessException;
import com.uu.enums.ErrorCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DevOpsAuthInterceptor implements HandlerInterceptor {

    @Value("${devops.auth.secretKey}")
    private String configSecretKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            DevOpsAuth annotation = handlerMethod.getMethodAnnotation(DevOpsAuth.class);

            if (annotation != null) {
                String secretKey = request.getHeader("X-DevOps-Secret-Key");

                if (secretKey == null || !secretKey.equals(configSecretKey)) {
                    throw new BusinessException(ErrorCodeEnum.FORBIDDEN);
                }
            }
        }
        return true;
    }
}