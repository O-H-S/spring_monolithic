package com.ohs.monolithic.user;

import com.ohs.monolithic.board.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;



    public Account create(String username, String email, String password) {
        return create(username, email, password, null, null);
    }

    public Account create(String username, String email, String password, String provider, String providerId) {
        Account user = new Account();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setProvider(provider);
        user.setProviderId(providerId);
        this.userRepository.save(user);
        return user;
    }

    public Account getAccount(String username) {
        Optional<Account> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

}