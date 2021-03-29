package com.sht.deal.config;

import com.sht.deal.interceoter.LoginIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private CorsInterceptor corsInterceptor;
    /**
     * 拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 跨域拦截器需放在最上面
        registry.addInterceptor(corsInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(new LoginIntercepter())
                .addPathPatterns("/api/token/*/**")
                //配置不拦截的api
                .excludePathPatterns("/api/user/save","/api/user/login","/api/user/loginAdmin","/api/user/authorError",
                        "/api/goods/findByPage","/api/goods/findById","/api/goods/findByLike",
                        "/api/classify1/findAll","/api/post/**", "/api/part-time/**")
        ;

        WebMvcConfigurer.super.addInterceptors(registry);
    }

    /**
     * 跨域处理
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")
                .allowCredentials(true).maxAge(3600);
    }

    /**
     * 静态资源映射配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/deal/**").addResourceLocations("file:/deal/");
    }
}
