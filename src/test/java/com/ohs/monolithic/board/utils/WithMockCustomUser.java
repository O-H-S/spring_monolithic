package com.ohs.monolithic.board.utils;

import com.ohs.monolithic.user.domain.Account;
import com.ohs.monolithic.user.domain.UserRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
  String username() default "user";
  String[] authorities() default {};
  //String nickname() default "user nickname";
  UserRole role() default UserRole.USER;
}