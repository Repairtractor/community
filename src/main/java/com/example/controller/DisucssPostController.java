package com.example.controller;

import com.example.entity.DiscussPost;
import com.example.entity.Page;
import com.example.entity.User;
import com.example.service.DiscussPostService;
import com.example.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DisucssPostController {

    @Autowired
    private DiscussPostService discus;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getDiscussPost(Model model, Page page) {
        page.setRows(discus.selectdiscussPostRows(0));
        page.setPath("index");


        System.out.println("hello world");
        List<DiscussPost> fields = discus.selectFields(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> result = new ArrayList<>();

        for (DiscussPost post : fields) {
            User user = userService.selectUserById(post.getUserId());
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            map.put("user", user);
            result.add(map);
        }
        System.out.println("hello world");
        model.addAttribute("result", result);

        return "index";
    }
}
