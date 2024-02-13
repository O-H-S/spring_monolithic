package com.ohs.monolithic.user.dto;

import com.ohs.monolithic.user.domain.Account;

import java.security.Principal;

public interface AppUser {
  Long getAccountId();
  Account getAccount();
}
