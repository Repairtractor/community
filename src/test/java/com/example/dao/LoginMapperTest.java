package com.example.dao;

import com.example.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class LoginMapperTest {

    @Autowired
    private LoginTicketMapper loginMap;

    @Test
    public void test() {
        loginMap.insertTicket(new LoginTicket(105,"sd",0,new Date()));

    }
    @Test
    public void test1(){
        LoginTicket ticket = loginMap.selectTicket("s3");
       // loginMap.updateTicket(ticket.getUserId(),"ssh");
    }
}
