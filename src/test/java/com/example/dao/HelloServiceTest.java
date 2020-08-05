package com.example.dao;

import com.example.service.HelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HelloServiceTest {

    @Autowired
    private HelloService helloService;


    @Test
    public void test() {
        helloService.save();
        helloService.save1();

    }

}
