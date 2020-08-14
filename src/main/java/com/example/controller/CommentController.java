package com.example.controller;

import com.example.entity.Comment;
import com.example.entity.DiscussPost;
import com.example.entity.Event;
import com.example.event.EventProducer;
import com.example.service.CommentService;
import com.example.service.DiscussPostService;
import com.example.util.CommunityConstant;
import com.example.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController  {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserThreadLocal users;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/add/{discussId}")
    public String add(@PathVariable("discussId") int discussId, Comment comment) {
        comment.setUserId(users.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //添加事件
        Event event = new Event()
                .setUserId(users.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setTopic(CommunityConstant.TOPIC_TYPE_COMMENT).setData("postId",discussId);
        //找到实体的主人
        if (comment.getEntityType()==CommunityConstant.POST_COMMENT){
            //如果评论的是一个帖子
            DiscussPost post = discussPostService.getDiscussPostById(discussId);
            event.setEntityUserId(post.getUserId());
        }else if (comment.getEntityType()==CommunityConstant.REPLY_COMMENT){
            //如果是评论的评论，就通过评论的评论id找到我评论的那个目标
            Comment com = commentService.getCommentById(comment.getEntityId());
            event.setEntityUserId(com.getUserId());
        }

        eventProducer.send(event);


        //有人评论，如果是回帖的话，需要修改回帖的数量，所以触发事件
        if (comment.getEntityType() ==CommunityConstant.POST_COMMENT){
            event=new Event().setUserId(users.getUser().getId())
                    .setEntityId(discussId).setEntityType(CommunityConstant.POST_COMMENT)
                    .setTopic(CommunityConstant.TOPIC_TYPE_POST);
            eventProducer.send(event);
        }




        return "redirect:/getPost/" + discussId;
    }

}












