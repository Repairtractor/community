package com.example.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
public class RedisConfigurationTest {

    @Autowired
    private RedisTemplate<String, Object> template;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void testString() {
        String content = "test:content";

        template.opsForValue().set(content, 1);
        System.out.println(template.opsForValue().get(content));
        template.opsForValue().increment(content, 100);
        System.out.println(template.opsForValue().get(content));

    }


    @Test
    public void test1() {
        String content = "test:content";

        //多次访问绑定key对象
        BoundValueOperations<String, Object> redisKey = template.boundValueOps(content);
        redisKey.increment(100);
        System.out.println(redisKey.get());

        //绑定对象
        BoundSetOperations<String, Object> ops = template.boundSetOps("text:ccc");

        //遍历对象
        ops.members().forEach(System.out::println);
    }

    @Test
    public void test2() {
        Object execute = template.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> redisOperations) throws DataAccessException {
                String content = "text:ccc";
                BoundSetOperations<String, Object> redisKey = template.boundSetOps(content);

                redisOperations.multi();  //开启事务
                redisKey.add("abc");
                return redisOperations.exec(); //执行
            }
        });

        //返回的是影响的行数，注意set是去重的
        System.out.println(execute);

        //绑定对象
        BoundSetOperations<String, Object> ops = template.boundSetOps("text:ccc");

        //遍历对象
        ops.members().forEach(System.out::println);
    }

    /**
     * 编程式事务
     */
    @Test
    public void test3(){
        transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                status.flush();
                return status;
            }
        });
    }


}













