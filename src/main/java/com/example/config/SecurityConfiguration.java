package com.example.config;

import com.example.util.CommunityConstant;
import com.example.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements CommunityConstant {
    //忽略那些路径
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resource/**");
    }

    //权限控制
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //声明路径
        http.authorizeRequests().antMatchers(
            "/comment/**","/follow","/like","/getMessage","/profile","upLoad","/addPost"
        ).hasAnyAuthority(AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR)
                .antMatchers( //置顶和加精只有版主能用
                        "/top","wonderful"
                ).hasAnyAuthority(AUTHORITY_MODERATOR)
                .antMatchers("/data/**").hasAnyAuthority(AUTHORITY_ADMIN)
                .antMatchers("/delete").hasAnyAuthority(AUTHORITY_ADMIN)
               .anyRequest().permitAll().and().csrf().disable() ;//除了这些请求以外的任何请求都可以,并且不启用csrf验证


        //配置当权限不够的时候
        http.exceptionHandling().accessDeniedHandler(new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                //获取头中的标识
                String header = request.getHeader("x-requested-with");
                if ("XMLHttpRequest".equals(header)) { //判断是不是异步请求
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    out.write(CommunityUtil.getJsonString(404, "你没有访问此功能的权限"));
                } else response.sendRedirect(request.getContextPath() + "/user/denied");
            }
        }).authenticationEntryPoint(new AuthenticationEntryPoint() { //没有登录时候的处理
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                //获取头中的标识
                String header = request.getHeader("x-requested-with");
                if ("XMLHttpRequest".equals(header)) { //判断是不是异步请求
                    response.setContentType("application/plain;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    out.write(CommunityUtil.getJsonString(404, "你还没有登录"));
                } else response.sendRedirect(request.getContextPath() + "/user/login");
            }
        });

        //绕过security底层的logout实现
        http.logout().logoutUrl("/security");
    }
}
