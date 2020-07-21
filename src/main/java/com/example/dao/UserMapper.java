package com.example.dao;

import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectUserById(int id);

    User selectUserByName(String userName);

    User selectUserByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String header);

    int updatePassword(int id, String password);
}
