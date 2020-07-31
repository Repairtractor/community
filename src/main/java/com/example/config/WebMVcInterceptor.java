package com.example.config;

import com.example.controller.interceptor.HelloInterceptor;
import com.example.controller.interceptor.LoginRequireInterceptor;
import com.example.controller.interceptor.UserIntereceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//讲拦截器配置到spring中，需要实现WebMvcConfigurer接口，然后重写addInterceptors
@Configuration
public class WebMVcInterceptor implements WebMvcConfigurer {

    @Autowired
    private HelloInterceptor helloInterceptor;

    @Autowired
    private UserIntereceptor userInterceptor;

    @Autowired
    private LoginRequireInterceptor requireInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.html");
        registry.addInterceptor(requireInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.html");
    }
}
