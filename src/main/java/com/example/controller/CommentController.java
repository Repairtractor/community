package com.example.controller;

import com.example.entity.Comment;
import com.example.service.CommentService;
import com.example.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserThreadLocal users;

    @PostMapping("/add/{id}")
    public String add(@PathVariable("id") int id, Comment comment) {
        System.out.println(comment.toString());
        comment.setUserId(users.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        return "redirect:/getPost/" + id;
    }

}












