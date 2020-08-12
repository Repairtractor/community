package com.example.entity;

import java.util.HashMap;
import java.util.Map;

//抽象事件对象
public class Event {
    private String topic; //事件主题
    private int userId; //触发时间的用户
    private int entityType; //触发的实体类型
    private int entityId;//触发的实体id
    private int entityUserId; //实体的主人
    private final Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        data.put(key, value);
        return this;
    }
}
