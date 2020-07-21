package com.example.dao;

import com.example.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void test(){
        User user = userMapper.selectUserById(11);
        System.out.println(user);

        System.out.println(userMapper.selectUserByName("liubei"));
        System.out.println(userMapper.selectUserByEmail("nowcoder23@sina.com"));

    }

    @Test
    public void test2(){
        User user = new User(1, "张三", "12345678", "0", "23", 1, 2, "2342", "23233", new Date());
        int num = userMapper.insertUser(user);
        if (num>0) System.out.println("添加成功");


        if (userMapper.updateHeader(150, "7777") > 0) System.out.println("修改成功");
        if (userMapper.updateStatus(150, 3) > 0) System.out.println("修改成功");
        if (userMapper.updatePassword(150, "7777") > 0) System.out.println("修改成功");

    }

}
