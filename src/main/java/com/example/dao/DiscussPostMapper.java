package com.example.dao;

import com.example.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     *
     * @param userId 用户Id 动态sql拼接，是否根据用户查询
     * @param offset 显示数据起点
     * @param limit 最大条目数 一页
     */
    List<DiscussPost> selectFields(int userId,int offset,int limit,int orderMode);

    /**
     *
     * @param userId 动态sql 通过用户查找帖子数量
     *
     */
    int selectDirectionRows(@Param("userId")int userId);

    int insertDirection(DiscussPost discussPost);

    DiscussPost findDiscussPostById(int id);

    int updateSetCommentCountById(int id,int commentCount);

    //置顶
    void updateTypeById(int id,int type);

    //加精
    void updateStatusById(int id,int status);

    //修改分数
    void updateScoreById(int id, double score  );

}
