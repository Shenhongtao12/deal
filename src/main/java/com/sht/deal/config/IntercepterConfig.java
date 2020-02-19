package com.sht.deal.config;

import com.sht.deal.interceoter.LoginIntercepter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@Configuration
public class IntercepterConfig implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(new LoginIntercepter()).addPathPatterns("/user/api/*/**");

		WebMvcConfigurer.super.addInterceptors(registry);
	}
}
