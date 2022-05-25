package com.ypw.websocketstarter.config;

import com.ypw.websocketstarter.properties.ConfigProperties;
import com.ypw.websocketstarter.properties.WebsocketRedisProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author hongmeng
 * @date 2022/5/24
 */
@ConditionalOnClass(WebsocketRedisProperties.class)
@Configuration
@EnableConfigurationProperties({WebsocketRedisProperties.class, ConfigProperties.class})
public class RedissonAutoConfiguration {
    @Bean(name = "websocketRedissonClient")
    RedissonClient websocketRedissonClient(WebsocketRedisProperties websocketRedisProperties) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(websocketRedisProperties.getHost())
                .setDatabase(websocketRedisProperties.getDatabase())
                .setConnectionPoolSize(200)
                .setPassword(websocketRedisProperties.getPassword())
                .setUsername(websocketRedisProperties.getUsername());
        return Redisson.create(config);
    }

    @Bean(name = "websocketConnectionFactory")
    public RedissonConnectionFactory websocketConnectionFactory(RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

    @Bean(name = "websocketRedisTemplate")
    public RedisTemplate<String, String> saleRedisTemplate(RedisConnectionFactory websocketConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        template.setConnectionFactory(websocketConnectionFactory);
        //key序列化方式
        template.setKeySerializer(redisSerializer);
        //value序列化
        template.setValueSerializer(redisSerializer);
        //value hashmap序列化
        template.setHashValueSerializer(redisSerializer);
        //key haspmap序列化
        template.setHashKeySerializer(redisSerializer);
        return template;
    }
}
