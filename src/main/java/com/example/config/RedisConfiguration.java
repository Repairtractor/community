package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate<> ();

        //设置序列化格式，就是java数据转换成redis格式的转换规则
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());

        //设置hash的转换格式
        template.setHashKeySerializer(RedisSerializer.string()  );
        template.setHashValueSerializer(RedisSerializer.json());

        //使设置生效
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        return template;
    }
}
