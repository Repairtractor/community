package com.example.event;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void send(Event event){
        //讲事件对象转换成字符串发送
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
