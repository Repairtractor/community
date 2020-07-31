package com.example.dao;

import com.example.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 查询帖子的所有回复
     * @param entityType 帖子的类型
     * @param entityId 帖子的id
     * @param offset 评论在数据库中的起始行
     * @param limit 一页最大评论值
     * @return 评论集合
     */
    List<Comment> getComments(int entityType,int entityId,int offset,int limit);

    /**
     * 查询评论的条目数
     * @param entityType
     * @param entityId
     * @return
     */
    int getCommentRows(int entityType,int entityId);


    int insertComment(Comment comment);

    Comment getCommentById(int id);


}
