package com.ohs.monolithic.auth.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ohs.monolithic.account.domain.UserRole;
import com.ohs.monolithic.auth.service.LocalAppUserDeserializer;
import com.ohs.monolithic.account.domain.Account;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;


import java.util.Collection;


@JsonDeserialize(using = LocalAppUserDeserializer.class)
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,isGetterVisibility = JsonAutoDetect.Visibility.NONE)
//@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalAppUser extends User implements AppUser {

  // 리팩토링 필요, Account 도메인 객체를 직접 가지기 보다는 dto를 사용하기.
  static GrantedAuthority ADMIN_AUTHORITY = new SimpleGrantedAuthority(UserRole.ADMIN.toString());

  @Setter
  private String nickname;

  @Setter
  private Long accountId;

  public LocalAppUser(Long accountId, String username, String password, String nickname ,Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.accountId = accountId;
    this.nickname = nickname;
  }

  @Override
  public Long getAccountId() {
    return accountId;
  }

  @Override
  public String getNickname() {
    return nickname;
  }

  @Override
  public Boolean isAdmin() {
   return this.getAuthorities().contains(ADMIN_AUTHORITY);
  }



  /*@JsonCreator
  public static LocalAppUser localAppUser(
          @JsonProperty("account") Account a,
          @JsonProperty("username") String b,
          @JsonProperty("password") String c,
          @JsonProperty("authorities") Collection<? extends GrantedAuthority> authorities
          ){
    return new LocalAppUser(a, b, c, authorities);
  }*/
}
