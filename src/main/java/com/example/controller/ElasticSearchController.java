package com.example.controller;

import com.example.entity.DiscussPost;
import com.example.entity.Page;
import com.example.service.ElasticSearchService;
import com.example.service.LikeService;
import com.example.service.UserService;
import com.example.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ElasticSearchController {

    @Autowired
    private ElasticSearchService EsService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @GetMapping(path = "/search")
    public String search(String keyword, Page page, Model model) {
        if (keyword == null) throw new IllegalArgumentException("关键字为空");

        org.springframework.data.domain.Page<DiscussPost> discussPosts = EsService.search(keyword, page.getCurrent() - 1, page.getLimit());

        if (discussPosts!=null){

            List<Map<String,Object>> res=new ArrayList<>();
            for (DiscussPost discussPost :discussPosts){
                Map<String,Object> map=new HashMap<>();
                map.put("user",userService.selectUserById(discussPost.getUserId()));
                map.put("post",discussPost);
                map.put("likeCount",likeService.getLikeCount(CommunityConstant.POST_COMMENT, discussPost.getId()));
                res.add(map);
            }

            model.addAttribute("discussPosts",res);
        }
        model.addAttribute("keyword",keyword);


        page.setPath("/search?keyword="+keyword);

        page.setRows(discussPosts==null?0:(int)discussPosts.getTotalElements());
        page.setLimit(10);

        return "site/search";
    }
}
