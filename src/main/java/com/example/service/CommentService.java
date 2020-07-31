package com.example.service;

import com.example.dao.CommentMapper;
import com.example.entity.Comment;
import com.example.util.CommunityConstant;
import com.example.util.SensitiveReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveReview sensitiveReview;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> getComments(int entityType, int entityId, int offset, int limit) {
        return commentMapper.getComments(entityType, entityId, offset, limit);
    }

    public int getCommentRows(int entityType, int entityId) {
        return commentMapper.getCommentRows(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        String sensitiveString = sensitiveReview.sensitiveString(comment.getContent());
        comment.setContent(sensitiveString);

        int num = commentMapper.insertComment(comment);
        if (comment.getEntityType()== CommunityConstant.POST_COMMENT) {
            int rows = commentMapper.getCommentRows(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateSetCommentCountById(comment.getEntityId(), rows);
        }

        return num;
    }

}









