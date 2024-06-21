package com.ohs.monolithic.auth.service;


import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.auth.domain.AppUser;
import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.Map;


@Deprecated
@Component
public class AppUserEntityMapper implements ApplicationListener<SessionDestroyedEvent> {
  Map<Long, AppUser> maps;

  @PostConstruct
  void init() {
    maps = new HashMap<>();
  }

  public void map(AppUser user) {
    maps.put(user.getAccountId(), user);
  }

  public void sync(Account entity){
    AppUser mapped = maps.get(entity.getId());
    if(mapped == null)
      return;

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCompletion(int status) {
        if(status == STATUS_COMMITTED) {
          //mapped.setAccount(entity);
        }
      }
    });

  }

  @Override
  public void onApplicationEvent(SessionDestroyedEvent event) {
    SecurityContext context = event.getSecurityContexts().get(0);
    if(context == null)
      return;
    Object principal = context.getAuthentication().getPrincipal();
    if(!(principal instanceof AppUser))
      return;

    AppUser appUser = (AppUser)principal;
    maps.remove(appUser.getAccountId());

  }
}


