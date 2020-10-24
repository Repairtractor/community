package com.example.service;

import com.example.dao.DiscussPostMapper;
import com.example.entity.DiscussPost;
import com.example.util.SensitiveReview;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper diMapper;

    @Autowired
    private SensitiveReview sensitiveReview;


    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    //cache的缓存接口类
    private LoadingCache<String, List<DiscussPost>> postListCache;
    private LoadingCache<Integer, Integer> postRowsCache;

    //初始化列表缓存
    @PostConstruct
    private void init() {
        postListCache = Caffeine.newBuilder().maximumSize(maxSize).expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    //查询数据的方法
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) throw new IllegalArgumentException("不能为空");
                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset=Integer.parseInt(params[0]);
                        int limit=Integer.parseInt(params[1]);

                        //放进缓存中
                        return diMapper.selectFields(0,offset,limit,1);
                    }
                });

        //查询数据的方法
        postRowsCache = Caffeine.newBuilder().maximumSize(maxSize).expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(key->diMapper.selectDirectionRows(key));
    }

    //帖子列表本地缓存
    //帖子总行数本地缓存

    /**
     * 查询所有的帖子
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<DiscussPost> selectFields(int userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == 1) {
            //这里会从缓存中查询，如果没有，就讲响应的数据放入缓存中，然后查询
            return postListCache.get(offset + ":" + limit);
        }
        logger.info("开始访问db");
        return diMapper.selectFields(userId, offset, limit, orderMode);
    }

    /**
     * 动态帖子数量/某个用户的帖子数量
     *
     * @param userId
     * @return
     */
    public int selectDiscussPostRows(int userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        return diMapper.selectDirectionRows(userId);
    }

    /**
     * 添加帖子
     *
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
    public DiscussPost getDiscussPostById(int id) {
        return diMapper.findDiscussPostById(id);
    }

    public int updateSetCommentCountById(int id, int commentCount) {
        return diMapper.updateSetCommentCountById(id, commentCount);
    }

    //置顶
    public void updateTypeById(int id, int type) {
        diMapper.updateTypeById(id, type);
    }

    //加精
    public void updateStatusById(int id, int status) {
        diMapper.updateStatusById(id, status);
    }


    public void updateScoreById(int id, double score) {
        diMapper.updateScoreById(id, score);
    }
}

















