package com.example.dao;

import com.example.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DiscussPostMapperTest {

    @Autowired
    private DiscussPostMapper discus;

    @Test
    public void test() {
        discus.selectFields(0, 0, 10).forEach(System.out::println);

        discus.selectFields(149, 0, 10).forEach(System.out::println);

        System.out.println(discus.selectDirectionRows(149));


    }

}
