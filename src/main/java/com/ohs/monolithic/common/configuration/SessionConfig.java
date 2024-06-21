package com.ohs.monolithic.common.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohs.monolithic.auth.service.OAuth2AppUserMixin;
import com.ohs.monolithic.auth.domain.OAuth2AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@RequiredArgsConstructor
@EnableRedisHttpSession
public class SessionConfig /*implements BeanClassLoaderAware*/ {


  // 주석은 principal에 Account 객체를 직접 참조했을 때의 직렬화 관련 코드이다. 현재는 변경됨
  //private ClassLoader loader;

  /*private ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
    mapper.addMixIn(OAuth2AppUser.class, OAuth2AppUserMixin.class);
    mapper.activateDefaultTyping(
            mapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY);
    return mapper;
  }*/

  /*@Bean
  public RedisSerializer<Object> springSessionDefaultRedisSerializer() {

    return new GenericJackson2JsonRedisSerializer(objectMapper());
  }*/
  /*
   * @see
   * org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang
   * .ClassLoader)
   */
  /*@Override
  public void setBeanClassLoader(ClassLoader classLoader) {
    this.loader = classLoader;
  }*/

 /* @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer(moduleConfig.objectMapper()));
    return template;
  }*/

  /*@Bean
  public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    return new Jackson2JsonRedisSerializer();
  }*/

}
