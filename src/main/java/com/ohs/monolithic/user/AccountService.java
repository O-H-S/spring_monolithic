package com.ohs.monolithic.user;

import com.ohs.monolithic.board.Comment;
import com.ohs.monolithic.board.exception.DataNotFoundException;
import com.ohs.monolithic.user.exception.FailedAdminLoginException;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin-key}")
    private String adminKey;

    public Account create(String username, String email, String password) {
        return create(username, email, password, null, null, UserRole.USER);
    }

    public Account create(String username, String email, String password, String provider, String providerId, UserRole role) {
        Account user = new Account();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setRole(role);
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

    public void upgradeToAdmin(String username, String typedAdminKey){
        Account siteUser = this.userRepository.findByusername(username).get();
        if(typedAdminKey.equals(adminKey)) {
            System.out.println(username + " is registered as admin");
            siteUser.setRole(UserRole.ADMIN);
            this.userRepository.save(siteUser);
        }
        else{
            throw new FailedAdminLoginException("invalid key");
        }

    }

}