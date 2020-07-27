package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cc")
public class HelloController {

    @RequestMapping(path = "/cookie", method = RequestMethod.GET)
    @ResponseBody
    public String name(HttpServletResponse response) {
        Cookie cookie = new Cookie("name2", "mynameisZhangSan");  //创建对象
        cookie.setPath("/cc"); //设置路径
        cookie.setMaxAge(60 * 10); //设置保存时间，单位为妙
        response.addCookie(cookie); //在响应头中添加cookie
        return "hello world";
    }

    @GetMapping(path = "/jj")
    @ResponseBody
    public String name1(@CookieValue("name") String name) {
        System.out.println(name);
        return name;
    }

    @GetMapping(path = "/love")
    @ResponseBody
    //创建session
    public String name2(HttpSession session) {
        session.setAttribute("id", 1);
        session.setAttribute("name", "love");
        return "hello world";
    }

    @GetMapping(path = "/getLove")
    @ResponseBody
    //接收session数据
    public String name3(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "hello world";
    }


}
