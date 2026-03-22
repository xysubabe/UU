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

    public static final String DEVOPS_ID_KEY = "devOpsId";
    private static final Long DEVOPS_ID = 999999L;

    @Value("${devops.auth.secretKey}")
    private String configSecretKey;

    private static final ThreadLocal<Long> DEVOPS_ID_HOLDER = new ThreadLocal<>();

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

                // 设置DevOps ID用于日志记录
                request.setAttribute(DEVOPS_ID_KEY, DEVOPS_ID);
                DEVOPS_ID_HOLDER.set(DEVOPS_ID);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        DEVOPS_ID_HOLDER.remove();
    }

    /**
     * 获取当前DevOps ID
     */
    public static Long getDevOpsId() {
        Long devOpsId = DEVOPS_ID_HOLDER.get();
        return devOpsId != null ? devOpsId : DEVOPS_ID;
    }
}