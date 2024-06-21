package com.ohs.monolithic.common.configuration;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@EnableCaching
public class CacheConfig {

    /*@Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("postCounts");
    }*/
    final ObjectMapper objectMapper;
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {

        ObjectMapper extendedMapper = objectMapper.copy();

        // 직렬화된 JSON 데이터가 다양한 타입의 객체를 포함할 수 있으므로, 역직렬화 시 정확한 타입으로 변환되어야 합니다. 타입 정보가 없다면, 기본적으로 LinkedHashMap으로 역직렬화될 수 있으며, 이는 ClassCastException을 유발
        // activateDefaultTyping : 직렬화/역직렬화 과정에서 객체의 타입 정보를 포함하도록
        // JsonTypeInfo.As.PROPERTY:  타입 정보를 JSON 속성으로 포함시킴. (@class 필드)
        extendedMapper = extendedMapper.activateDefaultTyping(extendedMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);


        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(extendedMapper);



        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .entryTtl(Duration.ofMinutes(1L));

        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(factory)
                .cacheDefaults(cacheConfig)
                .build();
    }

}