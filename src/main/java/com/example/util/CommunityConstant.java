package com.example.util;

public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认有效存储时间ticket
     */
    int DEFAULT_EXPIRED_SECOND=3600*12;

    /**
     * 记住状态下的ticket存储时间
     */
    int REMEMBER_EXPIRED_SECOND=3600*12*100;

    /**
     * 评论类型 帖子评论
     */
    int POST_COMMENT=1;

    int REPLY_COMMENT=2;
}
