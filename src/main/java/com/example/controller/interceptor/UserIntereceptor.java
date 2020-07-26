package com.example.controller.interceptor;

import com.example.entity.LoginTicket;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.util.CommunityUtil;
import com.example.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class UserIntereceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private UserThreadLocal users;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的ticket,并且查到user对象
        String ticket = CommunityUtil.getCookie(request, "ticket");
        System.out.println(ticket);

        if (ticket != null) {
            LoginTicket loginTicket = userService.selectTicket(ticket);
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                User user = userService.selectUserById(loginTicket.getUserId());
                if (user != null)
                    users.addUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = users.getUser();
        if (user != null && modelAndView != null)
            modelAndView.addObject("user", user);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (users.getUser() != null)
            users.remove();
    }
}
