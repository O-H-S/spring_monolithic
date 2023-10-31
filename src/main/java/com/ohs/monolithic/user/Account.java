package com.ohs.monolithic.user;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity

/*스프링 시큐리티에 이미 User 클래스가 있기 때문이다.
물론 패키지명이 달라 User라는 이름을 사용할수 있지만 패키지 오용으로 인한 오류가 발생할수 있으므로 이 책에서는 User 대신 SiteUser라는 이름으로 명명하였다.*/

public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true, nullable = true)
    private String email;

    @Column(nullable = true)
    private String provider;  // 소셜 로그인 제공자 (예: google, facebook, kakao)

    @Column(nullable = true)
    private String providerId;  // 소셜 로그인 제공자에서의 사용자 ID


    @Enumerated(EnumType.STRING)
    private UserRole role;
    @PostLoad
    protected void onCreate() {
        if (role == null) {
            role = UserRole.USER;
        }
    }


    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }


    public void setUsername(String username) {
        this.username = username;

    }

    public void setEmail(String email) {
        this.email = email;

    }

    public void setPassword(String encode) {
        this.password = encode;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

}