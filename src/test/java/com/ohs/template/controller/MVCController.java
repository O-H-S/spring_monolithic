package com.ohs.template.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/")
public class MVCController {

    public static class UserView{
        public String name;
        public int age;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {

        List<UserView> userList = Arrays.asList();
        model.addAttribute("users", userList);
        return "userList";
    }


    // 정적 내부 클래스 공부하기
    public static class UserCreationInput {
        public String name;
        public int age;
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute UserCreationInput user, Model model) {
        // 로직
        return "redirect:/web/users";
        /*
            redirect:<URL> - URL로 리다이렉트 (리다이렉트는 완전히 새로운 URL로 요청이 된다.)
            forward:<URL> - URL로 포워드 (포워드는 기존 요청 값들이 유지된 상태로 URL이 전환된다.)
        */
    }

    // 사용자가 /?name=test 로 요청한다면, 위 핸들러의 매개변수인 name 에 "test"가 매핑됩니다.
    @PostMapping("/create")
    public String createUser(@RequestParam("name") String name) {
        System.out.println("이름 : " + name);
        return "test";
    }

    @PostMapping("/create")
    public String createUserFromBody(@RequestBody String createInfo) {
        return "test";
    }

}