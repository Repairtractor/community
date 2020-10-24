package com.example.service;

import com.example.util.CommunityRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    //添加uv 通过ip存储
    public void addUv(String ip) {
        String redisKey = CommunityRedis.getSingleUv(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    public long getUv(Date start, Date end) {
        if (start == null || end == null) throw new IllegalArgumentException("日期参数不能为空");

        List<String> keyList = new ArrayList<>();

        //时间处理对象
        Calendar calendar = Calendar.getInstance();
        //添加时间初始值
        calendar.setTime(start);

        //循环时间在结束时间之前，下列循环对时间进行处理，天数增加处理
        while (!calendar.after(end)) {
            //从calendar对象中取出时间
            String redisKey = CommunityRedis.getSingleUv(df.format(calendar.getTime()));
            keyList.add(redisKey);
            calendar.add(Calendar.DATE, 1);
        }

        //获取到日期的从开始到结束的所有redisKey之后，就可以合并了
        String redisKey = CommunityRedis.getDoubleUv(df.format(start), df.format(end));

        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray(new String[0]));

        //返回合并之后，开始到结束的不重复数据量
        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    //通过用户id 存储dau
    public void addDau(int userId) {
        String redisKey = CommunityRedis.getSingleDau(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    //获取日期范围内的dau
    public long getDau(Date start, Date end) {
        if (start == null || end == null) throw new IllegalArgumentException("日期参数不能为空");

        List<byte[]> keyList = new ArrayList<>();

        //时间处理对象
        Calendar calendar = Calendar.getInstance();
        //添加时间初始值
        calendar.setTime(start);

        //循环时间在结束时间之前，下列循环对时间进行处理，天数增加处理
        while (!calendar.after(end)) {
            //从calendar对象中取出时间
            String redisKey = CommunityRedis.getSingleDau(df.format(calendar.getTime()));
            keyList.add(redisKey.getBytes());
            calendar.add(Calendar.DATE, 1);
        }

        //合并日期内的key，做or运算，因为只要用户在这日期范围内有登录访问，就算活跃
        return (long) redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                String redisKey = CommunityRedis.getDoubleDau(df.format(start), df.format(end));
                redisConnection.bitOp(RedisStringCommands.BitOperation.OR, redisKey.getBytes(), keyList.toArray(new byte[0][0]));
                return redisConnection.bitCount(redisKey.getBytes());
            }
        });

    }


}
