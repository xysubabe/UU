package com.uu.config;

import com.uu.interceptor.DevOpsAuthInterceptor;
import com.uu.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DevOpsAuthInterceptor devOpsAuthInterceptor;

    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 不需要登录校验的路径
     */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/auth/**",
            "/file/**",
            "/region/**",
            "/payment/callback",
            "/error"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // DevOps认证拦截器
        registry.addInterceptor(devOpsAuthInterceptor)
                .addPathPatterns("/devops/**", "/payment/mock-pay");

        // 登录校验拦截器
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATHS);
    }
}