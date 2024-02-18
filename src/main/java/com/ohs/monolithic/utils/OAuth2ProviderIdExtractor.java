package com.ohs.monolithic.utils;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2ProviderIdExtractor {
  String getProviderName();
  String extract(OAuth2User target);
}
