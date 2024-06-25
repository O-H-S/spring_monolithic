package com.ohs.monolithic.utils;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.account.domain.UserRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

  String username() default "user";
  String[] authorities() default {};
  String nickname() default "MockLoggedUser";
  UserRole role() default UserRole.USER;
}