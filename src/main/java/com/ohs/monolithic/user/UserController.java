package com.ohs.monolithic.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final AccountService service;

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
}