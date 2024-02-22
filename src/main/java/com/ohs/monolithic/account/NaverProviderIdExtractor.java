package com.ohs.monolithic.account;

import com.ohs.monolithic.common.utils.OAuth2ProviderIdExtractor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class NaverProviderIdExtractor implements OAuth2ProviderIdExtractor {


  @Override
  public String getProviderName() {
    return "naver";
  }

  @Override
  public String extract(OAuth2User target) {
    var att = target.getAttributes();
    LinkedHashMap<?,?> response = (LinkedHashMap<?,?>)att.get("response");
    return (String)response.get("id");
  }
}
