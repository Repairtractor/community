package com.example.controller;

import com.example.entity.Event;
import com.example.entity.Page;
import com.example.entity.User;
import com.example.event.EventProducer;
import com.example.service.FollowService;
import com.example.service.UserService;
import com.example.util.CommunityConstant;
import com.example.util.CommunityUtil;
import com.example.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController {

    @Autowired
    private UserThreadLocal users;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    @Autowired
    private EventProducer eventProducer;


    @PostMapping(path = "/followee")
    @ResponseBody
    public String attentional(int entityType, int entityId) {
        User user = users.getUser();

        followService.attentional(user.getId(), entityId, entityType);

        //触发关注消息,关注的用户，entityId就是用户id,userId是触发消息的那个人
        Event event=new Event().setTopic(CommunityConstant.TOPIC_TYPE_FOLLOW).setUserId(user.getId())
                .setEntityType(entityType).setEntityId(entityId).setEntityUserId(entityId);

        eventProducer.send(event);

        return CommunityUtil.getJsonString(0, "已经关注");
    }

    @PostMapping(path = "/unFollowee")
    @ResponseBody
    public String unAttentional(int entityType, int entityId) {
        User user = users.getUser();

        followService.unAttentional(user.getId(), entityId, entityType);

        return CommunityUtil.getJsonString(0, "已经取消关注");
    }

    //http://localhost:8080/community/getFolloweeList/11
    @GetMapping(path = "/getFollowee/{userId}")
    public String getFolloweeList(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.selectUserById(userId);
        if (user == null) throw new IllegalArgumentException("用户不存在");

        model.addAttribute("target", user);
        //传入用户和实体类型,找到我关注的用户
        page.setRows((int) followService.followCount(userId, CommunityConstant.USER_COMMENT));
        page.setPath("/getFollowee/" + userId);
        page.setLimit(5);

        List<Map<String, Object>> followeeList = followService.getFolloweeList(userId, page.getOffset(), page.getLimit());

        for (Map<String, Object> map : followeeList) {
            User u = (User) map.get("user");
            map.put("isFollow", isFollow(userId));
        }

        model.addAttribute("follows", followeeList);
        return "site/followee";
    }

    @GetMapping(path = "/getFollower/{userId}")
    public String getFollowerList(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.selectUserById(userId);
        if (user == null) throw new IllegalArgumentException("用户不存在");
        model.addAttribute("target", user);
        //传入实体ID和实体类型
        page.setRows((int) followService.followerCount(userId, CommunityConstant.USER_COMMENT));
        page.setPath("/getFollower/" + userId);
        page.setLimit(5);

        List<Map<String, Object>> followerList = followService.getFollowerList(userId, page.getOffset(), page.getLimit());

        if (followerList == null) throw new RuntimeException("没有查到");

        //查询每一个关注我的用户的关注状态
        for (Map<String, Object> map : followerList) {
            User u = (User) map.get("user");
            map.put("isFollow", isFollow(userId));
        }

        model.addAttribute("followers", followerList);
        return "site/follower";
    }

    //查看我是否关注了这个用户
    private boolean isFollow(int userId) {
        if (users.getUser() == null) return false;
        return followService.isFollow(users.getUser().getId(), CommunityConstant.USER_COMMENT, userId);
    }


}









