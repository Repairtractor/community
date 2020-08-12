package com.example.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    //aop切点 指定切入的对象和方法
    @Pointcut("execution(* com.example.service.*.*(..))")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) { //joinpoint是需要执行的对象
        //用户某某在某个时间访问了什么方法

        //获取ip地址
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        //这里因为消费者自动调用service，并不是通过controller，所以取不到ip，可能为空
        if (attributes==null)return;

        HttpServletRequest servletRequest = attributes.getRequest();
        String host = servletRequest.getRemoteHost();

        //调用SimpleDateFormat对象格式化日期
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        //获得执行的类 和方法
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("%s用户在%s,访问了%s", host, format, target));
    }


}
