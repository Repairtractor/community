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

    int USER_COMMENT=3;

    /**
     * 事件/消息的类型主题  kafka
     */
    String TOPIC_TYPE_LIKE="like";
    String TOPIC_TYPE_COMMENT="comment";
    String TOPIC_TYPE_FOLLOW="follow";
    String TOPIC_TYPE_POST="post";
    String TOPIC_TYPE_DELETE="delete";

    /**
     * 系统发送消息人
     */
    int SYSTEM_SEND_MESSAGE_MAN =1;


    /**
     * 添加访问权限，用户 管理 版主
     */
    String AUTHORITY_USER="user";
    String AUTHORITY_ADMIN="admin";
    String AUTHORITY_MODERATOR="moderator";

}
