package com.ohs.monolithic.account.service;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.account.domain.LocalCredential;
import com.ohs.monolithic.account.dto.AccountResponse;
import com.ohs.monolithic.account.event.AccountDataChangeEvent;
import com.ohs.monolithic.auth.domain.LocalAppUser;
import com.ohs.monolithic.account.repository.LocalCredentialRepository;
import com.ohs.monolithic.auth.service.AppUserEntityMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@RequiredArgsConstructor
@Service
public class LocalAccountService implements UserDetailsService {


    private final LocalCredentialRepository localCredentialRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<LocalCredential> credentialOp = localCredentialRepository.findByUsername(username);

        if (credentialOp.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }

        LocalCredential credential = credentialOp.get();
        Account account = credential.getAccount();
        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority(account.getRole().toString()));


        LocalAppUser appUser = new LocalAppUser(account.getId(), credential.getUsername(), credential.getPassword(), account.getNickname(), authorities);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            request.setAttribute("loggedAccountData", AccountResponse.of( account));  // "currentUser" 속성에 appUser 객체를 설정
        }

        return appUser;
    }

    //@Transactional( readOnly = true, propagation = Propagation.REQUIRES_NEW)
    //@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener
    public void reload(AccountDataChangeEvent event){
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth instanceof UsernamePasswordAuthenticationToken) {
            LocalAppUser oldPrincipal = (LocalAppUser) currentAuth.getPrincipal();
            UserDetails newPrincipal = loadUserByUsername(oldPrincipal.getUsername());
            Authentication newAuth = new UsernamePasswordAuthenticationToken(newPrincipal, null, currentAuth.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}