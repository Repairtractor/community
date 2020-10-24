package com.example.controller;

import com.example.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/data")
public class DataController {

    @Autowired
    private DataService dataService;

    @RequestMapping(path = "/list",method = {RequestMethod.GET,RequestMethod.POST})
    public String data(){
        return "site/admin/data";
    }

    @RequestMapping(path = "/getUv",method = RequestMethod.POST)
    public String getUv(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start, @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model){
        long uv = dataService.getUv(start, end);
        model.addAttribute("uvData",uv);
        model.addAttribute("uvStart",start);
        model.addAttribute("uvEnd",end);
        return "forward:/data/list";  //转发，不写forwadr是直接返回模板给前端控制器，加了就是将请求转发给另一个请求
    }

    @RequestMapping(path = "/getDau",method = RequestMethod.POST)
    public String getDau(@DateTimeFormat(pattern = "yyyy-MM-dd")Date start, @DateTimeFormat(pattern = "yyyy-MM-dd")Date end, Model model){
        long dau = dataService.getDau(start, end);
        model.addAttribute("DauData",dau);
        model.addAttribute("dauStart",start);
        model.addAttribute("dauEnd",end);
        return "forward:/data/list";  //转发，不写forwar是直接返回末班给前端控制器，加了就是将请求转发给另一个请求
    }
}
