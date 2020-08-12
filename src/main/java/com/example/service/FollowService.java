package com.example.service;

import com.example.entity.User;
import com.example.util.CommunityConstant;
import com.example.util.CommunityRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserService userService;


    //关注
    public void attentional(int userId, int entityId, int entityType) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                String followeeString = CommunityRedis.getPrefixFolloweeString(userId, entityType);
                String prefixFollowerString = CommunityRedis.getPrefixFollowerString(entityId, entityType);

                redisOperations.multi();

                redisTemplate.opsForZSet().add(followeeString, entityId, System.currentTimeMillis());
                redisTemplate.opsForZSet().add(prefixFollowerString, userId, System.currentTimeMillis());
                return redisOperations.exec();
            }
        });
    }

    //取消关注
    public void unAttentional(int userId, int entityId, int entityType) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                String followeeString = CommunityRedis.getPrefixFolloweeString(userId, entityType);
                String prefixFollowerString = CommunityRedis.getPrefixFollowerString(entityId, entityType);

                redisOperations.multi();

                redisTemplate.opsForZSet().remove(followeeString, entityId);
                redisTemplate.opsForZSet().remove(prefixFollowerString, userId);
                return redisOperations.exec();
            }
        });
    }

    //查询关注的实体数量
    public long followCount(int userId, int entityType) {
        String followeeString = CommunityRedis.getPrefixFolloweeString(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeString);
    }

    //查询某个实体粉丝的数量
    public long followerCount(int entityId, int entityType ) {
        String followerString = CommunityRedis.getPrefixFollowerString(entityId, entityType);
        return redisTemplate.opsForZSet().zCard(followerString);
    }


    //查询当前用户是否关注了entityId用户
    public boolean isFollow(int userId, int entityType, int entityId) {
        String followeeString = CommunityRedis.getPrefixFolloweeString(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeString,entityId)!=null;
    }


    //查询某个用户关注的用户
    public List<Map<String,Object>> getFolloweeList(int userId,int offset,int limit){
        User user = userService.selectUserById(userId);
        if (user == null)throw new IllegalArgumentException("当前用户不存在");


        List<Map<String, Object> > list = new ArrayList<>();

        String followeeString = CommunityRedis.getPrefixFolloweeString(userId, CommunityConstant.USER_COMMENT);

        //reverseRange按照score倒序查询，参数为long，所以强转，offset开始，后面是截止的位置，所以加上offset-1
        Set<Object> follows = redisTemplate.opsForZSet().reverseRange(followeeString, (int) offset, (int) limit + offset - 1);


        if (follows !=null){
            for (Object obj:follows){
                if (obj instanceof Integer){
                    Map<String,Object> map=new HashMap<>();
                    int targetId=(Integer)obj;
                    User u = userService.selectUserById(targetId);
                    map.put("user",u);
                    Double score = redisTemplate.opsForZSet().score(followeeString, targetId);
                    Date date = new Date(score.longValue());
                    map.put("dateTime",date);
                    list.add(map);
                }
            }
        }
        return list;
    }

    //查询关注某个用户的用户
    public List<Map<String,Object>> getFollowerList(int userId,int offset,int limit){
        User user = userService.selectUserById(userId);
        if (user == null)throw new IllegalArgumentException("当前用户不存在");


        List<Map<String, Object> > list = new ArrayList<>();

        String followerString = CommunityRedis.getPrefixFollowerString(userId,CommunityConstant.USER_COMMENT);

        //reverseRange按照score倒序查询，参数为long，所以强转，offset开始，后面是截止的位置，所以加上offset-1
        Set<Object> follows = redisTemplate.opsForZSet().reverseRange(followerString, (int) offset, (int) limit + offset - 1);


        if (follows !=null){
            for (Object obj:follows){
                if (obj instanceof Integer){
                    Map<String,Object> map=new HashMap<>();
                    int targetId=(Integer)obj;
                    User u = userService.selectUserById(targetId);
                    map.put("user",u);
                    Double score = redisTemplate.opsForZSet().score(followerString, targetId);
                    Date date = new Date(score.longValue());
                    map.put("dateTime",date);
                    list.add(map);
                }
            }
        }
        return list;
    }




}











