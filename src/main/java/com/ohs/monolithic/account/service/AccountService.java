package com.ohs.monolithic.account.service;

import com.ohs.monolithic.account.domain.*;
import com.ohs.monolithic.account.dto.AccountCreateForm;
import com.ohs.monolithic.account.dto.AccountResponse;
import com.ohs.monolithic.account.event.AccountDataChangeEvent;
import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.account.exception.FailedAccountCreationException;
import com.ohs.monolithic.account.exception.FailedAdminLoginException;
import com.ohs.monolithic.account.repository.AccountRepository;
import com.ohs.monolithic.account.repository.LocalCredentialRepository;
import com.ohs.monolithic.account.repository.OAuth2CredentialRepository;
import com.ohs.monolithic.common.exception.PermissionException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository userRepository;
    private final LocalCredentialRepository localCredentialRepository;
    private final OAuth2CredentialRepository oauth2CredentialRepository;
    private final PasswordEncoder passwordEncoder;

    final ApplicationEventPublisher eventPublisher;

    @Value("${app.admin-key}")
    private String adminKey;

    @Transactional
    public AccountResponse createAccount(AccountCreateForm form){


        if (!form.getPassword().equals(form.getPassword2())){
            throw new FailedAccountCreationException("비밀번호 확인 값이 불일치합니다.");
        }
        Account result = createAsLocal(form.getNickname(), form.getEmail(), form.getUsername(), form.getPassword());
        return AccountResponse.of(result);
    }


    //  "엔티티를 스프링이나 외부 캐시에 저장하면 절대! 안됩니다. 엔티티는 영속성 컨텍스트에서 상태를 관리하기 때문에, 항상 DTO로 변환해서 변환한 DTO를 캐시에 저장해서 관리해야 합니다."
    // 높은 동시성 환경에서 고비용 연산을 캐싱하는 경우 sync = true 설정을 고려할 수 있음.
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "accounts", key = "#id")
    public AccountResponse getAccount(Long id, AppUser user){
        if (id == null || id < 0) {
            throw new IllegalArgumentException("올바르지 않은 account id 입니다.");
        }
        Optional<Account> byId = userRepository.findById(id);
        if(byId.isEmpty())
            throw new EntityNotFoundException("존재하지 않는 계정입니다.");
        //log.info("");
        return AccountResponse.of(byId.get());
    }


    @Transactional
    public Account createAsLocal(String nickname, String email, String username, String password){
        Account newAccount = Account.builder()
                .authenticationType(AuthenticationType.Local)
                .nickname(nickname)
                .email(email)
                .role(UserRole.USER)
                .build();
        try {
            userRepository.save(newAccount);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException cException && cException.getSQLState().equals( "23000")) {
                throw new FailedAccountCreationException("중복된 닉네임입니다.");
            }
        }


        LocalCredential newCredential = LocalCredential.builder()
                .account(newAccount)
                .username(username)
                .password(passwordEncoder.encode(password))
                .build();

        try{
            localCredentialRepository.save(newCredential);
        }catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException cException && cException.getSQLState().equals( "23000")) {
                throw new FailedAccountCreationException("중복된 아이디입니다.");
            }
        }
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

        try {
            userRepository.save(newAccount);
        } catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException cException && cException.getSQLState().equals( "23000")) {
                throw new FailedAccountCreationException("중복된 닉네임입니다.");
            }
        }

        OAuth2Credential newCredential = OAuth2Credential.builder()
                .account(newAccount)
                .provider(provider)
                .providerId(providerId)
                .build();

        try {
            oauth2CredentialRepository.save(newCredential);
        }catch(DataIntegrityViolationException e) {
            if(e.getCause() instanceof ConstraintViolationException cException && cException.getSQLState().equals( "23000")) {
                throw new FailedAccountCreationException("이미 가입된 외부 계정입니다.");
            }
        }
        return newAccount;
    }

    @Transactional
    public void upgradeToAdmin(AppUser user, String typedAdminKey){

        if(typedAdminKey.equals(adminKey)) {
            Optional<Account> accountOp = userRepository.findById(user.getAccountId());
            if(accountOp.isEmpty())
                return;
            Account account = accountOp.get();

            System.out.println(account.getNickname() + " is registered as admin");

            account.setRole(UserRole.ADMIN);
            this.userRepository.save(account);

            eventPublisher.publishEvent(new AccountDataChangeEvent());

        }
        else{
            throw new FailedAdminLoginException("invalid key");
        }

    }

    public void assertPermission(AppUser operator, Long targetId){
        if(!operator.getAccountId().equals(targetId) && !operator.isAdmin())
            throw new PermissionException("(Account Failed) 해당 요청의 권한이 없습니다.");
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


        eventPublisher.publishEvent(new AccountDataChangeEvent());
        return AccountResponse.of(target);
    }



}