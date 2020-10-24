package com.example.quartz;

import com.example.entity.DiscussPost;
import com.example.service.CommentService;
import com.example.service.DiscussPostService;
import com.example.service.ElasticSearchService;
import com.example.service.LikeService;
import com.example.util.CommunityConstant;
import com.example.util.CommunityRedis;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job, CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private CommentService commentService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ElasticSearchService elasticSearchService;

    //网站时间
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014--08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败" + e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey= CommunityRedis.getScore();
        BoundSetOperations<String,Object> operation= redisTemplate.boundSetOps(redisKey);

        if (operation.size()==0){
            logger.info("任务取消，没有需要刷新的帖子");
            return;
        }

        logger.info("任务开始，正在开始刷新任务"+operation.size()    );

        while (operation.size() > 0){
            this.refresh((Integer)operation.pop());
        }


        logger.info("帖子刷新完毕");


    }

    //取出postId 进行分数计算
    private void refresh(int pop) {
        DiscussPost post = discussPostService.getDiscussPostById(pop);
        if (post==null){
            logger.info("该帖子不存在");
            return;
        }

        //是否加精
        boolean wonderful= post.getStatus() == 1;

        //评论数量
        int commentCount = post.getCommentCount();

        //点赞数量
        long likeCount= likeService.getLikeCount(POST_COMMENT,pop);

        //计算权重
        double w=(wonderful?75:0)+commentCount*10+likeCount*2;
        //分数=权重+距离天数
        double score=Math.log1p(Math.max(w,1))
                +(post.getCreateTime().getTime()-epoch.getTime())*(1000*3600*24);

        //更新帖子分数
        discussPostService.updateScoreById(pop,score);

        //同步搜索数据
        post.setScore(score);
        elasticSearchService.insertEsPost(post);
    }
}
















