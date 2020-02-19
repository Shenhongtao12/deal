package com.sht.deal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 资源映射路径
 */
@Configuration
public class UserImgConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/deal/**").addResourceLocations("file:/deal/");
        //registry.addResourceHandler("/deal/**").addResourceLocations("file:F:/deal/");
    }
}