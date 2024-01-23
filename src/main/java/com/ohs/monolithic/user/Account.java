package com.ohs.monolithic.user;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

@Getter
@Entity
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true)
    private String username;

    @Setter
    private String password;

    @Setter
    @Column(unique = true, nullable = true)
    private String email;

    @Setter
    @Column(nullable = true)
    private String provider;  // 소셜 로그인 제공자 (예: google, facebook, kakao)

    @Setter
    @Column(nullable = true)
    private String providerId;  // 소셜 로그인 제공자에서의 사용자 ID


    @Setter
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @PostLoad
    protected void onCreate() {
        if (role == null) {
            role = UserRole.USER;
        }
    }

    @Builder
    public Account(String username, String password, String email, UserRole role){
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;

    }

    @Builder(builderClassName = "WithProvider", builderMethodName = "WithProvider")
    public Account(String provider, String providerId, UserRole role){
        this.username = provider + providerId;
        this.password = null;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }


}