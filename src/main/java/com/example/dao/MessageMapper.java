package com.example.dao;

import com.example.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //根据userid找到用户的所有会话，只寻找每个会话的最后一条信息
    List<Message> selectConversations(int userId, int offset, int limit);

    //查找会话的数量
    int selectConversationRows(int userId);

    //查找单个会话包含的私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);

    //查找单个会话所包含的私信个数
    int selectLetterCount(String conversationId);

    //查询未读私信数量,动态sql拼接
    int selectLetterUnReadCount(int userId,String conversationId);

    int insertMessage(Message message);

    int updateMessageStatus(List<Integer> ids,int status);

}
