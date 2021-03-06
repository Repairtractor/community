package com.example.service;

import com.example.dao.MessageMapper;
import com.example.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    public List<Message> selectConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int insertMessage(Message message){
        return messageMapper.insertMessage(message);
    }

    public int updateMessageStatus(List<Integer> ids,int status){
        return messageMapper.updateMessageStatus(ids, status);
    }


    public int selectConversationRows(int userId) {
        return messageMapper.selectConversationRows(userId);
    }


    public List<Message> selectLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }


    public int selectLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    public int selectLetterUnReadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnReadCount(userId, conversationId);
    }

    public Message selectLatestMessage(int userId,String topic){
        return messageMapper.selectLatestMessage(userId, topic);
    }

    //查询每个主题所包含的通知数量
   public int selectMessageCount(int userId,String topic){
        return messageMapper.selectMessageCount(userId, topic);
   }

    //查询未读的数量
   public int selectUnReadCount(int userId, String topic){
        return messageMapper.selectUnReadCount(userId, topic);
   }

    //查询消息详情
   public List<Message> selectAllMessage(int userId,String topic,int offset,int limit){
        return messageMapper.selectAllMessage(userId, topic, offset, limit);
   }

}
