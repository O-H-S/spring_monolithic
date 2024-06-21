package com.ohs.monolithic.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

@Configuration
@RequiredArgsConstructor
@EnableRedisRepositories
public class RedisConfig {
  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${spring.data.redis.password:#{null}}")
  private String password;


  final ModuleConfig moduleConfig;
  @Bean
  public RedisConnectionFactory redisConnectionFactory(){
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(host);
    redisStandaloneConfiguration.setPort(port);
    if(password != null)
      redisStandaloneConfiguration.setPassword(password);
    return new LettuceConnectionFactory(redisStandaloneConfiguration);
  }
  @Bean
  public ConfigureRedisAction configureRedisAction() {
    // Spring Session이 Redis 서버에 대해 CONFIG 명령을 실행하지 않도록 설정하는 방법

    // 레디스 CONFIG 명령 실행 방지 (AWS Elasticache 등 일부 환경에서 필요)

    /*Spring Session은 Redis의 설정을 자동으로 조정하여 성능 및 안정성을 향상시키려고 시도할 수 있습니다. ConfigureRedisAction.NO_OP을 설정하면 이러한 자동 조정이 발생하지 않지만, 이는 일반적으로 큰 영향을 미치지 않습니다.*/

    return ConfigureRedisAction.NO_OP;
  }


  // "템플릿 메소드 패턴(Template Method Pattern)"을 구현하는 데서 그 이름이 유래합니다. 이 패턴은 객체지향 디자인에서 특정 작업을 수행하는 과정에서 고정된 프로세스를 정의하고, 변화하는 부분만 서브클래스에서 확장할 수 있게 하는 구조를 말합니다.
 /* @Bean
  public StringRedisTemplate stringRedisTemplate() {
    StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();

    stringRedisTemplate.setConnectionFactory(redisConnectionFactory());

    stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
    stringRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    stringRedisTemplate.setDefaultSerializer(new StringRedisSerializer());
    stringRedisTemplate.afterPropertiesSet();
    return stringRedisTemplate;
  }*/


}