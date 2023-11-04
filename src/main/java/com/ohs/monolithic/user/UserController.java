package com.ohs.monolithic.user;

import com.ohs.monolithic.user.exception.FailedAdminLoginException;
import com.ohs.monolithic.user.jwt.TokenInfo;
import jakarta.persistence.PostLoad;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final AccountService service;

    @PreAuthorize("isAuthenticated()")
    //@PatchMapping("/me/admin")
    @PostMapping("/me/admin")
    public String tryLoginToAdmin(@AuthenticationPrincipal UserDetails user, @RequestParam String secretKey){
        System.out.println("trying to get admin role : " + secretKey);
        try {
            service.upgradeToAdmin(user.getUsername(), secretKey);
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            List<GrantedAuthority> updatedAuthorities = AuthorityUtils.createAuthorityList(UserRole.ADMIN.toString());

            if (currentAuth instanceof UsernamePasswordAuthenticationToken) {
                // 일반 로그인 사용자
                UserDetails userDetails = (UserDetails) currentAuth.getPrincipal();
                Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, null, updatedAuthorities);
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            } else if (currentAuth instanceof OAuth2AuthenticationToken) {
                // OAuth2 로그인 사용자
                OAuth2User oauth2User = (OAuth2User) currentAuth.getPrincipal();
                OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(oauth2User, updatedAuthorities, ((OAuth2AuthenticationToken) currentAuth).getAuthorizedClientRegistrationId());
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }

        }
        catch(FailedAdminLoginException e){
            System.out.println("failed to get role");
        }
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signup(AccountCreateForm accountCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid AccountCreateForm accountCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        //ClassPathScanningCandidateComponentProvider

        if (!accountCreateForm.getPassword1().equals(accountCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }
        try {
            service.create(accountCreateForm.getUsername(),
                    accountCreateForm.getEmail(), accountCreateForm.getPassword1());
        }
        catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }
        catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }
        //bindingResult.reject(오류코드, 오류메시지)는 특정 필드의 오류가 아닌 일반적인 오류를 등록할때 사용한다.
        return "redirect:/";
    }

    // 실제 로그인을 진행하는 @PostMapping 방식의 메서드는 스프링 시큐리티가 대신 처리하므로 직접 구현할 필요가 없다.
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @PostMapping("api/login") // form 기반 토큰
    public String loginAsToken(@RequestParam String username,
                               @RequestParam String password,
                               HttpServletResponse response){

        System.out.println(username + " " + password);
        TokenInfo tokenInfo = service.GetJwtToken(username, password);
        System.out.println(tokenInfo.getAccessToken());
        // JWT 토큰을 HttpOnly, Secure 쿠키로 설정
        // ResponseCookie는 뭐지?
        //Cookie jwtCookie = new Cookie("jwt-token", String.format("Bearer %s", tokenInfo.getAccessToken())); 쿠기에 공백불가
        Cookie jwtCookie = new Cookie("jwt-token", tokenInfo.getAccessToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // HTTPS에서만 쿠키 전송
        jwtCookie.setPath("/");
        //jwtCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키 유효기간 설정 (예: 7일)
        response.addCookie(jwtCookie);
        return "redirect:/";
    }
}