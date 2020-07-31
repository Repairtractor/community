package com.example.controller.interceptor;

import com.example.annotation.LoginRequir;
import com.example.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginRequireInterceptor implements HandlerInterceptor {

    @Autowired
    private UserThreadLocal users;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handler1 = (HandlerMethod) handler;
            LoginRequir requir = handler1.getMethodAnnotation(LoginRequir.class);
            if (requir != null && users.getUser() == null)
                response.sendRedirect(request.getContextPath() + "/user/login");

        }
        return true;
    }
}
