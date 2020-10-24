package com.example.controller.interceptor;

import com.example.service.DataService;
import com.example.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private UserThreadLocal users;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ip = request.getRemoteHost();
        dataService.addUv(ip);
        if (users.getUser() != null) dataService.addDau(users.getUser().getId());
        return true;
    }
}
