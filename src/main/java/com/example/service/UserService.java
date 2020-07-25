package com.example.service;

import com.example.dao.UserMapper;
import com.example.entity.User;
import com.example.util.CommunityConstants;
import com.example.util.CommunityUtil;
import com.example.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User selectUserById(int id) {
        return userMapper.selectUserById(id);
    }

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUserName())) {
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 验证账号
        User u = userMapper.selectUserByName(user.getUserName());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在!");
            return map;
        }

        // 验证邮箱
        u = userMapper.selectUserByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());

        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/user/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public CommunityConstants acativation(int userId, String code) {
        User user = userMapper.selectUserById(userId);
        if (user.getStatus() == 1) return CommunityConstants.ACTIVATION_REPEAT;
        else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(user.getId(),1);
            return CommunityConstants.ACTIVATION_SUCCESS;
        }
        else return CommunityConstants.ACTIVATION_FAILURE;
    }


    public User selectUserByName(String userName) {
        return userMapper.selectUserByName(userName);
    }

    public User selectUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    public int updateStatus(int id, int status) {
        return userMapper.updateStatus(id, status);
    }

    public int updateHeader(int id, String header) {
        return userMapper.updateHeader(id, header);
    }

    public int updatePassword(int id, String password) {
        return userMapper.updatePassword(id, password);
    }

}