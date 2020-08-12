package com.example.service;

import com.example.dao.DiscussPostMapper;
import com.example.dao.UserMapper;
import com.example.entity.DiscussPost;
import com.example.entity.User;
import com.example.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

public class HelloService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private TransactionTemplate trans;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public String save() {
        return getString();
    }


    public String save1() {
        trans.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        trans.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return trans.execute(transaction -> getString());
    }

    private String getString() {
        User user = new User(1, "hello", "123", CommunityUtil.generateUUID().substring(0, 5), "hello@163.com", 0, 0, "1234", "jjj", new Date());
        userMapper.insertUser(user);

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle("jjj");
        discussPost.setContent("jj");
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDirection(discussPost);

        return "ok";
    }


}
