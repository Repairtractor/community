package com.example.controller;

import com.example.entity.Event;
import com.example.entity.User;
import com.example.event.EventProducer;
import com.example.service.LikeService;
import com.example.util.CommunityConstant;
import com.example.util.CommunityRedis;
import com.example.util.CommunityUtil;
import com.example.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private UserThreadLocal users;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     *
     * @param entityType 被点赞类型
     * @param entityId 被点赞id
     * @param targetId 被点赞用户
     * @param postId 被点赞帖子id
     * @return
     */
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId,int targetId,int postId) {
        User user = users.getUser();


        likeService.like(user.getId(), entityType, entityId,targetId);

        //点赞数量
        int likeCount = likeService.getLikeCount(entityType, entityId);

        //是否已赞
        int likeStatus = likeService.getLikeStatus(user.getId(), entityType, entityId);

        //触发点赞事件
        if (likeStatus!=0){
            Event event=new Event().setTopic(CommunityConstant.TOPIC_TYPE_LIKE).setUserId(user.getId()).setEntityId(entityId)
                    .setEntityType(entityType).setEntityUserId(targetId).setData("postId",postId);
            eventProducer.send(event);
        }

        if (entityType==CommunityConstant.POST_COMMENT){
            //增加分数
            String redisKey= CommunityRedis.getScore();
            redisTemplate.opsForSet().add(redisKey,postId);
        }



        Map<String, Object> map = new HashMap<>();

        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJsonString(0, null, map);
    }

}
