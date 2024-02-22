package com.ohs.monolithic.account.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    ;

    private final String value;
}
// ORDINAL 순서가 흐뜨러져 데이터의 정합성 문제가 발생할 수 있으므로, @Enumerated(EnumType.STRING)와 같이 문자열로 사용 권장.