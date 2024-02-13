package com.ohs.monolithic.user.service;

import com.ohs.monolithic.user.domain.Account;
import com.ohs.monolithic.user.domain.LocalCredential;
import com.ohs.monolithic.user.dto.LocalAppUser;
import com.ohs.monolithic.user.repository.AccountRepository;
import com.ohs.monolithic.user.repository.LocalCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(account.getRole().toString()));

        return new LocalAppUser(account, credential.getUsername(), credential.getPassword(), authorities);
    }
}