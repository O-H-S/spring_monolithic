package com.ohs.monolithic;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohs.monolithic.auth.service.OAuth2AppUserMixin;
import com.ohs.monolithic.auth.domain.OAuth2AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.jackson2.SecurityJackson2Modules;

import java.nio.charset.StandardCharsets;

public class SerializerTest {

  @Test
  public void test(){
    RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer(objectMapper());

    /*String target = "{\"@class\":\"org.springframework.security.core.context.SecurityContextImpl\",\"authentication\":{\"@class\":\"org.springframework.security.authentication.UsernamePasswordAuthenticationToken\",\"authorities\":[\"java.util.Collections$UnmodifiableRandomAccessList\",[{\"@class\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"ADMIN\"}]],\"details\":{\"@class\":\"org.springframework.security.web.authentication.WebAuthenticationDetails\",\"remoteAddress\":\"0:0:0:0:0:0:0:1\",\"sessionId\":\"3986166f-6c9d-4128-aebd-764eedac62c5\"},\"authenticated\":true,\"principal\":{\"@class\":\"com.ohs.monolithic.auth.domain.LocalAppUser\",\"password\":null,\"username\":\"abcd\",\"authorities\":[\"java.util.Collections$UnmodifiableSet\",[{\"@class\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"ADMIN\"}]],\"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true,\"enabled\":true,\"account\":{\"@class\":\"com.ohs.monolithic.account.domain.Account\",\"id\":5,\"nickname\":\"test2dfsssdf\",\"email\":\"dfsd3fsd@naver.com\",\"profileImage\":\"24692b02ae19aa9d787d3b887ba11a43\",\"role\":\"ADMIN\",\"authenticationType\":\"Local\",\"createDate\":[2024,4,12,12,54,54,316339000]}},\"credentials\":null}}";*/

    String target = "{\"@class\":\"org.springframework.security.core.context.SecurityContextImpl\",\"authentication\":{\"@class\":\"org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken\",\"principal\":{\"@class\":\"com.ohs.monolithic.auth.domain.OAuth2AppUser\",\"authorities\":[\"java.util.Collections$UnmodifiableSet\",[{\"@class\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"USER\"}]],\"attributes\":{\"@class\":\"java.util.Collections$UnmodifiableMap\",\"sub\":\"100474978443254947504\",\"email_verified\":true,\"name\":\"HS O\",\"given_name\":\"HS\",\"family_name\":\"O\",\"picture\":\"https://lh3.googleusercontent.com/a/ACg8ocJBGJsW1fMr4zCT6tlBlqFq3OWGik6TI0205_nwYeJ_z3KifQ=s96-c\",\"email\":\"ohhyunsu0606@gmail.com\"},\"nameAttributeKey\":\"sub\",\"account\":{\"@class\":\"com.ohs.monolithic.account.domain.Account\",\"id\":44,\"nickname\":\"test8\",\"email\":\"\",\"profileImage\":\"776f5a7ee84bd7bffb949510bd57083e\",\"role\":\"USER\",\"authenticationType\":\"OAuth2\",\"createDate\":[2024,4,19,9,28,2,401307000]}},\"authorities\":[\"java.util.Collections$UnmodifiableRandomAccessList\",[{\"@class\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"USER\"}]],\"authorizedClientRegistrationId\":\"google\",\"details\":{\"@class\":\"org.springframework.security.web.authentication.WebAuthenticationDetails\",\"remoteAddress\":\"0:0:0:0:0:0:0:1\",\"sessionId\":\"b491aaed-c980-407f-aa8d-40f72ccad5ee\"}}}";

    Object result =  serializer.deserialize(target.getBytes(StandardCharsets.UTF_8));
    SecurityContextImpl casted = (SecurityContextImpl)result;
    Object principal = casted.getAuthentication().getPrincipal();
  }

  private ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();

    /*SimpleModule mo = new SimpleModule();
    mo.addDeserializer(LocalAppUser.class, new LocalAppUserDeserializer());
    mapper.registerModules(mo);*/

    mapper.addMixIn(OAuth2AppUser.class, OAuth2AppUserMixin.class);
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    mapper.registerModules(SecurityJackson2Modules.getModules(classLoader));

    mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    return mapper;
  }
}
