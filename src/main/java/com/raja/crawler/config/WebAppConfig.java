package com.raja.crawler.config;

import com.raja.crawler.interceptor.AllowCorsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    private final AllowCorsInterceptor allowCorsInterceptor;

    public WebAppConfig(AllowCorsInterceptor allowCorsInterceptor) {
        this.allowCorsInterceptor = allowCorsInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(allowCorsInterceptor).addPathPatterns("/**");
    }
}
