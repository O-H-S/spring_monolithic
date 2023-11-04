package com.ohs.monolithic.user;

import com.ohs.monolithic.board.exception.DataNotFoundException;
import com.ohs.monolithic.user.exception.FailedAdminLoginException;
import com.ohs.monolithic.user.jwt.JwtTokenProvider;
import com.ohs.monolithic.user.jwt.TokenInfo;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;


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

    public TokenInfo GetJwtToken(String username, String password){
        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        return tokenInfo;
    }

}