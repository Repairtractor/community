package com.example.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.stream.IntStream;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test() {
        String redisKey = "test:hll", redisKey2 = "test:hll:33", redisKey3 = "test:hll:44";
        redisTemplate.opsForHyperLogLog().delete(redisKey);

        IntStream.range(0, 100000).forEach(i -> redisTemplate.opsForHyperLogLog().add(redisKey, i));
        Long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);

        IntStream.range(5000, 150000).forEach(i -> redisTemplate.opsForHyperLogLog().add(redisKey2, i));

        Long number = redisTemplate.opsForHyperLogLog().size(redisKey);

        redisTemplate.opsForHyperLogLog().union(redisKey3, redisKey, redisKey2);
        number = redisTemplate.opsForHyperLogLog().size(redisKey3);

        System.out.println(number);
    }

    @Test
    public void test2() {
        String redisKey1 = "bitmap:1", redisKey2 = "bitmap:2", redisKey3 = "bitmap:3";

        redisTemplate.opsForValue().setBit(redisKey2, 1, true);
        redisTemplate.opsForValue().setBit(redisKey2, 2, true);
        redisTemplate.opsForValue().setBit(redisKey2, 3, true);

        redisTemplate.opsForValue().setBit(redisKey3, 3, true);
        redisTemplate.opsForValue().setBit(redisKey3, 4, true);
        redisTemplate.opsForValue().setBit(redisKey3, 5, true);

        //左or运算  redist获取连接的回调函数
        Object num = redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                //获取某个值中true的数量
                // return connection.bitCount(redisKey2.getBytes());

                //使用哪种运算符，结果存入那个，需要作运算的数组
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey1.getBytes()
                        , redisKey2.getBytes(), redisKey3.getBytes());

                return connection.bitCount(redisKey1.getBytes());
            }
        });

        System.out.println(num);
        System.out.println(redisTemplate.opsForValue().getBit(redisKey1,1));

    }
}













