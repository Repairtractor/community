package com.example.service;

import com.example.dao.UserMapper;
import com.example.entity.User;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User selectUserById(int id) {
        return userMapper.selectUserById(id);
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
