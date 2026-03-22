package com.uu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WechatConfig {

    private MiniappConfig miniapp;
    private PayConfig pay;

    @Data
    public static class MiniappConfig {
        private String appid;
        private String secret;
    }

    @Data
    public static class PayConfig {
        private String appid;
        private String mchId;
        private String apiKey;
        private String apiV3Key;
        private String certPath;
        private String notifyUrl;
    }
}