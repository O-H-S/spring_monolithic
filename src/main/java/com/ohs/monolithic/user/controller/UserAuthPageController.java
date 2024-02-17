package com.ohs.monolithic.user.controller;

import com.ohs.monolithic.user.domain.UserRole;
import com.ohs.monolithic.user.dto.AccountCreateForm;
import com.ohs.monolithic.user.dto.AppUser;
import com.ohs.monolithic.user.exception.FailedAdminLoginException;
import com.ohs.monolithic.user.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserAuthPageController {

    private final AccountService service;

    @PreAuthorize("isAuthenticated()")
    //@PatchMapping("/me/admin")
    @PostMapping("/me/admin")
    public String tryLoginToAdmin(@AuthenticationPrincipal AppUser user, @RequestParam String secretKey){
        System.out.println("trying to get admin role : " + secretKey);
        try {
            service.upgradeToAdmin(user.getAccount(), secretKey);
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
        

        if (!accountCreateForm.getPassword1().equals(accountCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }
        try {
            service.createAsLocal(accountCreateForm.getNickname(), accountCreateForm.getEmail(), accountCreateForm.getUsername(), accountCreateForm.getPassword1());
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


        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }
}