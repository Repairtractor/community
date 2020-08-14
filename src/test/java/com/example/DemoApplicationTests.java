package com.example;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DemoApplicationTests {

    @Test
   public void contextLoads() {
        Logger logger= LoggerFactory.getLogger(this.getClass());
        logger.info("hello world");
    }


    
}
