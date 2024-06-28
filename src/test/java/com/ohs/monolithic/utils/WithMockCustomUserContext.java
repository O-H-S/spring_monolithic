package com.ohs.monolithic.utils;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.auth.domain.AppUser;

public class WithMockCustomUserContext {
  static final ThreadLocal<Account> accountHolder = new ThreadLocal<>();
  static final ThreadLocal<AppUser> appUserHolder = new ThreadLocal<>();
  public static void setAccount(Account account) {
    accountHolder.set(account);
  }
  public static void setAppUser(AppUser appUser) {
    appUserHolder.set(appUser);
  }
  public static Account getAccount() {
    return accountHolder.get();
  }
  public static AppUser getAppUser() {
    return appUserHolder.get();
  }
  public static void clear(){
    accountHolder .remove();
    appUserHolder.remove();
  }
}
