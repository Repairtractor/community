package com.example.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DiscussPostMapperTest {

    @Autowired
    private DiscussPostMapper discus;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void test() {
        discus.selectFields(0, 0, 10).forEach(System.out::println);

        discus.selectFields(149, 0, 10).forEach(System.out::println);

        System.out.println(discus.selectDirectionRows(149));


    }

    @Test
    public void test2(){
        messageMapper.selectAllMessage(111,"comment",0,5).forEach(System.out::println);
    }

}
