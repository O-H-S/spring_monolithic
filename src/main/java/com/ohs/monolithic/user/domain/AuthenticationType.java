package com.ohs.monolithic.user.domain;

public enum AuthenticationType {
  Local, // 서버 db username/password 비교를 통한 인증
  OAuth2, // 외부 플랫폼으로부터 OAuth2를 통한 인증.
}
