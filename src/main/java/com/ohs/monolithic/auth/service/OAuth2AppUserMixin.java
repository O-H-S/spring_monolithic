package com.ohs.monolithic.auth.service;

import com.fasterxml.jackson.annotation.*;
import com.ohs.monolithic.account.domain.Account;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;


@Deprecated
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
abstract public class OAuth2AppUserMixin {
  @JsonCreator
  OAuth2AppUserMixin( @JsonProperty("provider") String provider,  @JsonProperty("providerId") String providerId, @JsonProperty("accountId") Long accountId, @JsonProperty("nickname") String nickname ,@JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities,
                     @JsonProperty("attributes") Map<String, Object> attributes,
                     @JsonProperty("nameAttributeKey") String nameAttributeKey) {
  }

}