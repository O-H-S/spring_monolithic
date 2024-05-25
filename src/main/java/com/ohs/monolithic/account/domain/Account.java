package com.ohs.monolithic.account.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String nickname;

    @Setter
    @Column(unique = false, nullable = true)
    private String email;

    @Setter
    @Column(unique = false, nullable = true)
    private String profileImage;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Setter
    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;


    @Setter
    @Column(name = "create_date")
    //@JsonDeserialize(using = LocalDateTimeDeserializer.class)
    //@JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDate;

    @Builder/*(builderClassName = "AsLocal", builderMethodName = "AsLocal")*/
    public Account(String nickname, String email, UserRole role, AuthenticationType authenticationType, LocalDateTime createDate){

        if (createDate == null)
            createDate = LocalDateTime.now();

        this.nickname = nickname;
        this.email = email;
        this.authenticationType = authenticationType;
        this.role = role;
        this.createDate = createDate;

    }

/*
    @Builder(builderClassName = "AsOAuth2", builderMethodName = "AsOAuth2")
    public Account(String nickname, String email, UserRole role){

        this.role = role;
    }
*/


}