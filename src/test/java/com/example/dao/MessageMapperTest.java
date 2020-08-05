package com.example.dao;

import com.example.entity.Message;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class MessageMapperTest {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MessageMapper messageMapper;

    @Test
    public void test() {
        List<Message> list = null;

        int count = messageMapper.selectConversationRows(111);
        System.out.println(count);

        list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);
        count = messageMapper.selectLetterUnReadCount(131, "111_131");
        System.out.println(count);

    }
}
