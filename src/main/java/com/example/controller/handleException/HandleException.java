package com.example.controller.handleException;


import com.example.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class HandleException {

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(HandleException.class);

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("发送异常:" + e.getMessage());  //获取异常的名字
        for (StackTraceElement element : e.getStackTrace()) logger.error(element.toString()); //把异常取出，添加进日志

        //获取头中的标识
        String header = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(header)) { //判断是不是异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.write(CommunityUtil.getJsonString(1, "错误了"));
        } else response.sendRedirect(request.getContextPath() + "/error");

    }

}
