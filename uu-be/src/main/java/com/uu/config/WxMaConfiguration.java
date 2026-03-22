package com.uu.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.uu.config.WechatConfig.MiniappConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置
 */
@Configuration
public class WxMaConfiguration {

    @Autowired
    private WechatConfig wechatConfig;

    @Bean
    public WxMaService wxMaService() {
        MiniappConfig miniapp = wechatConfig.getMiniapp();

        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(miniapp.getAppid());
        config.setSecret(miniapp.getSecret());

        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);

        return service;
    }
}