package com.ohs.monolithic.user.service;

import com.ohs.monolithic.board.exception.DataNotFoundException;
import com.ohs.monolithic.user.domain.*;
import com.ohs.monolithic.user.exception.FailedAdminLoginException;
import com.ohs.monolithic.user.repository.AccountRepository;
import com.ohs.monolithic.user.repository.LocalCredentialRepository;
import com.ohs.monolithic.user.repository.OAuth2CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository userRepository;
    private final LocalCredentialRepository localCredentialRepository;
    private final OAuth2CredentialRepository oauth2CredentialRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin-key}")
    private String adminKey;


    @Transactional
    public Account createAsLocal(String nickname, String email, String username, String password){
        Account newAccount = Account.builder()
                .authenticationType(AuthenticationType.Local)
                .nickname(nickname)
                .email(email)
                .role(UserRole.USER)
                .build();

        LocalCredential newCredential = LocalCredential.builder()
                .account(newAccount)
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();

        userRepository.save(newAccount);
        localCredentialRepository.save(newCredential);
        return newAccount;
    }

    @Transactional
    public Account createAsOAuth2(String nickname, String email, String provider, String providerId){
        Account newAccount = Account.builder()
                .authenticationType(AuthenticationType.OAuth2)
                .nickname(nickname)
                .email(email)
                .role(UserRole.USER)
                .build();

        OAuth2Credential newCredential = OAuth2Credential.builder()
                .account(newAccount)
                .provider(provider)
                .providerId(providerId)
                .build();

        userRepository.save(newAccount);
        oauth2CredentialRepository.save(newCredential);
        return newAccount;
    }

    public void upgradeToAdmin(Account target, String typedAdminKey){

        if(typedAdminKey.equals(adminKey)) {
            System.out.println(target.getNickname() + " is registered as admin");
            target.setRole(UserRole.ADMIN);
            this.userRepository.save(target);
        }
        else{
            throw new FailedAdminLoginException("invalid key");
        }

    }

}