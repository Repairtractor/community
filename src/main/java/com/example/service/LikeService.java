package com.example.service;

import com.example.util.CommunityRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate<String, Object> template;

    //点赞
    public void like(int userId, int entityType, int entityId, int targetId) {
        template.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                //获取redis中的key
                String keyName = CommunityRedis.getKeyName(entityType, entityId);
                String keyUserName = CommunityRedis.getKeyName(targetId);

                //判断是否已经赞过这个用户
                Boolean isMember = template.opsForSet().isMember(keyName, userId);

                redisOperations.multi();
                if (isMember) {
                    template.opsForSet().remove(keyName, userId);
                    template.opsForValue().increment(keyUserName);
                } else {
                    template.opsForSet().add(keyName, userId);
                    template.opsForValue().decrement(keyUserName);
                }
                return redisOperations.exec();
            }
        });
    }

    public int likeCount(int userId){
        String keyUserName = CommunityRedis.getKeyName(userId);

        Object nums=template.opsForValue().get(keyUserName);
        if (nums==null)return 0;
        return (Integer)nums;
    }

    //查看点赞数量
    public int getLikeCount(int entityType, int entityId) {
        String keyName = CommunityRedis.getKeyName(entityType, entityId);
        return template.opsForSet().size(keyName).intValue();
    }

    //点赞的状态，已赞还是未赞
    public int getLikeStatus(int userId, int entityType, int entityId) {
        String keyName = CommunityRedis.getKeyName(entityType, entityId);
        return template.opsForSet().isMember(keyName, userId) ? 1 : 0;
    }

}
