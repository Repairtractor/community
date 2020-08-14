package com.example.controller.interceptor;

import com.example.service.MessageService;
import com.example.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private UserThreadLocal users;

    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (users.getUser() != null && modelAndView!=null) {
            int letterUnReadCount = messageService.selectLetterUnReadCount(users.getUser().getId(), null);
            int count = messageService.selectUnReadCount(users.getUser().getId(), null);
            modelAndView.addObject("allUnReadCount", letterUnReadCount + count);
        }
    }
}
