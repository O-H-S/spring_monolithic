package com.ohs.monolithic.user.domain;

import com.ohs.monolithic.board.BoardPaginationType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = false)
    private String nickname;

    @Setter
    @Column(unique = false, nullable = true)
    private String email;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Setter
    @Enumerated(EnumType.STRING)
    private AuthenticationType authenticationType;

    @Builder/*(builderClassName = "AsLocal", builderMethodName = "AsLocal")*/
    public Account(String nickname, String email, UserRole role, AuthenticationType authenticationType){
        this.nickname = nickname;
        this.email = email;
        this.authenticationType = authenticationType;
        this.role = role;
    }

/*
    @Builder(builderClassName = "AsOAuth2", builderMethodName = "AsOAuth2")
    public Account(String nickname, String email, UserRole role){

        this.role = role;
    }
*/


}