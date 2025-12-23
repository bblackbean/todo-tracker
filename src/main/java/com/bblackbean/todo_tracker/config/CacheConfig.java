package com.bblackbean.todo_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory cf) {

        // 기본 TTL 60분
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(60));

        // 날씨 캐시 TTL만 10분으로
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put("weather", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(cf)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .build();
    }
}
