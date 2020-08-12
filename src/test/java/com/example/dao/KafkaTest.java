package com.example.dao;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@SpringBootTest
public class KafkaTest {
    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void test(){
        kafkaProducer.send("test","你好");
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        kafkaProducer.send("test","你好");
    }

}

@Component
class KafkaProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void send(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}

//自动监听
@Component
class KafkaConsumer {
    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }
}
