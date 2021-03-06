package com.example.util;

public class CommunityRedis {
    private static final String SPLIT_STRING = ":";

    //点赞redis前缀，里面存储set<userid> 谁点的赞
    private static final String PREFIX_ENTITY_STRING = "like:entity";
    private static final String PREFIX_USER_STRING = "like:user";

    //某个用户关注的某个实体 followee:userid:entityType
    private static final String PREFIX_FOLLOWEE_STRING="followee";

    //查看关注的粉丝
    private static final String PREFIX_FOLLOWER_STRING="follower";

    //存储验证码
    private static final String PREFIX_KAPTCHA_STRING="kaptcha";

    //存储用户登录凭证
    private static final String PREFIX_TICKET_STRING="loginTicket";

    //存储用户信息
    private static final  String PREFIX_USERS_STRING="users";


    //存储已赞的帖子和userID  key:(like:entity:entityType:entityId) value: (userId)
    public static String getKeyName(int entityType, int entityId) {
        return PREFIX_ENTITY_STRING + SPLIT_STRING + entityType + SPLIT_STRING + entityId;
    }

    //存储userId和他收到的点赞数量 key(like:user:userId) value:(数量)
    public static String getKeyName(int userId) {
        return PREFIX_USER_STRING + SPLIT_STRING + userId;
    }

    //存储关注的实体，followee:userId:entityType->entityId,date
    public static String getPrefixFolloweeString(int userId,int entityType){
        return PREFIX_FOLLOWEE_STRING+SPLIT_STRING+userId+SPLIT_STRING+entityType;
    }
    //存储关注某个实体的粉丝 follower:entityId:entityType->userId date
    public static String getPrefixFollowerString(int entityId,int entityType){
        return PREFIX_FOLLOWER_STRING+SPLIT_STRING+entityId+SPLIT_STRING+entityType;
    }

    //存储验证码，存储形式为 kaptcha:uuid->验证码
    public static String getKaptchaString(String uuid){
        return PREFIX_KAPTCHA_STRING+SPLIT_STRING+uuid;
    }

    //存储登录凭证，存储形式为loginTicket:ticket->loginTicket对象
    public static String getTicketString(String ticket){
        return PREFIX_TICKET_STRING+SPLIT_STRING+ticket;
    }

    //存储用户，存储形式为users:userId->user
    public static  String getUsersString(int userId){
        return PREFIX_USERS_STRING+SPLIT_STRING+userId;
    }



}
