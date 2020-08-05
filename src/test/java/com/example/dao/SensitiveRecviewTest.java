package com.example.dao;

import com.example.util.SensitiveReview;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveRecviewTest {
    @Autowired
    private SensitiveReview sensitiveReview;

    @Test
    public void test(){
        String string = sensitiveReview.sensitiveString("hello,🀐赌博，嫖🀙娼");
        System.out.println(string);
    }
}
