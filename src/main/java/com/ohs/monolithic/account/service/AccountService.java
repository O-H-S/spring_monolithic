package com.ohs.monolithic.account.service;

import com.ohs.monolithic.account.AppUserEntityMapper;
import com.ohs.monolithic.account.domain.*;
import com.ohs.monolithic.account.dto.AccountResponse;
import com.ohs.monolithic.account.dto.AppUser;
import com.ohs.monolithic.account.exception.FailedAdminLoginException;
import com.ohs.monolithic.account.repository.AccountRepository;
import com.ohs.monolithic.account.repository.LocalCredentialRepository;
import com.ohs.monolithic.account.repository.OAuth2CredentialRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository userRepository;
    private final AppUserEntityMapper userPrincipalMapper;
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

        userRepository.save(newAccount);
        LocalCredential newCredential = LocalCredential.builder()
                .account(newAccount)
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();


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

        userRepository.save(newAccount);

        OAuth2Credential newCredential = OAuth2Credential.builder()
                .account(newAccount)
                .provider(provider)
                .providerId(providerId)
                .build();


        oauth2CredentialRepository.save(newCredential);
        return newAccount;
    }

    @Transactional
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

    public void assertPermission(AppUser operator, Long targetId){
        if(!operator.getAccountId().equals(targetId) && !operator.isAdmin())
            throw new AccessDeniedException("(Account Failed) 해당 요청의 권한이 없습니다.");
    }
    @Transactional
    public AccountResponse updateAccount(Long id, Map<String, String> data, AppUser operator){

        assertPermission(operator, id);

        Optional<Account> targetOp = userRepository.findById(id);
        if(targetOp.isEmpty())
            throw new EntityNotFoundException("(Account Patch Failed) 대상 계정이 존재하지 않습니다.");

        Account target = targetOp.get();

        String targetNickname = data.get("nickname");
        if(targetNickname!=null) {
            if(targetNickname.equals(target.getNickname()))
                throw new IllegalArgumentException("기존 닉네임과 같습니다.");
            target.setNickname(targetNickname);
        }



        String targetEmail = data.get("email");
        if(targetEmail != null)
            target.setEmail(targetEmail);

        String targetProfileImage = data.get("profileImage");
        if(targetProfileImage != null)
            target.setProfileImage(targetProfileImage);


        userRepository.save(target);


        return AccountResponse.of(target);
    }



}