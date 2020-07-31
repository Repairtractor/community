package com.example.service;

import com.example.dao.DiscussPostMapper;
import com.example.entity.DiscussPost;
import com.example.util.SensitiveReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper diMapper;

    @Autowired
    private SensitiveReview sensitiveReview;

    /**
     * 查询所有的帖子
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<DiscussPost> selectFields(int userId, int offset, int limit) {
        return diMapper.selectFields(userId, offset, limit);
    }

    /**
     * 动态帖子数量/某个用户的帖子数量
     *
     * @param userId
     * @return
     */
    public int selectDiscussPostRows(int userId) {
        return diMapper.selectDirectionRows(userId);
    }

    /**
     * 添加帖子
     * @return
     */
    public int insertDiscussPost(DiscussPost post) {
        if (post == null) throw new IllegalArgumentException("参数不能为空");
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        post.setContent(sensitiveReview.sensitiveString(post.getContent()));
        post.setTitle(sensitiveReview.sensitiveString(post.getTitle()));

        return diMapper.insertDirection(post);
    }

    /**
     * 查询某个帖子
     */
    public DiscussPost getDiscussPostById(int id){
        return diMapper.findDiscussPostById(id);
    }

    public int updateSetCommentCountById(int id,int commentCount){
        return diMapper.updateSetCommentCountById(id, commentCount);
    }
}

















