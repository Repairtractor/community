package com.example.event;

import com.alibaba.fastjson.JSONObject;
import com.example.dao.DiscussPostMapper;
import com.example.entity.DiscussPost;
import com.example.entity.Event;
import com.example.entity.Message;
import com.example.service.ElasticSearchService;
import com.example.service.MessageService;
import com.example.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息消费者
 */
@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private ElasticSearchService EsService;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @KafkaListener(topics = {TOPIC_TYPE_LIKE, TOPIC_TYPE_COMMENT, TOPIC_TYPE_FOLLOW})
    public void handleCommentMessage(ConsumerRecord<String, String> consumerRecord) {

        if (consumerRecord == null || consumerRecord.value() == null) {
            logger.error("消息错误");
            return;
        }

        //接收收到的消息，转成对象，这里注意转换方法
        Event event =  JSONObject.parseObject(consumerRecord.value().toString(),Event.class);
        if (event == null) {
            logger.error("消息格式不正确");
            return;
        }

        //创建一个message对象
        Message message = new Message();
        message.setFromId(SYSTEM_SEND_MESSAGE_MAN);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        //用map来集合所有需要的数据放进content中
        Map<String, Object> map = new HashMap<>();
        map.put("userId", event.getUserId());
        map.put("entityType", event.getEntityType());
        map.put("entityId", event.getEntityId());

        //取出event map中的数据，放进content中
        if (!event.getData().isEmpty())
            for (Map.Entry<String, Object> entry : event.getData().entrySet())
                map.put(entry.getKey(), entry.getValue());

        message.setContent(JSONObject.toJSONString(map));
        messageService.insertMessage(message);

    }

    @KafkaListener(topics = {TOPIC_TYPE_POST})
    public void handlePostMessage(ConsumerRecord<String, String> consumerRecord) {

        if (consumerRecord == null || consumerRecord.value() == null) {
            logger.error("消息错误");
            return;
        }

        //接收收到的消息，转成对象，这里注意转换方法
        Event event =  JSONObject.parseObject(consumerRecord.value().toString(),Event.class);
        if (event == null) {
            logger.error("消息格式不正确");
            return;
        }

        int entityId = event.getEntityId();
        DiscussPost post = discussPostMapper.findDiscussPostById(entityId);

        EsService.insertEsPost(post);
    }


    @KafkaListener(topics = {TOPIC_TYPE_DELETE})
    public void handleTopMessage(ConsumerRecord<String, String> consumerRecord) {

        if (consumerRecord == null || consumerRecord.value() == null) {
            logger.error("消息错误");
            return;
        }

        //接收收到的消息，转成对象，这里注意转换方法
        Event event =  JSONObject.parseObject(consumerRecord.value().toString(),Event.class);
        if (event == null) {
            logger.error("消息格式不正确");
            return;
        }

        int entityId = event.getEntityId();

        EsService.deleteEsPost(entityId);
    }







}
