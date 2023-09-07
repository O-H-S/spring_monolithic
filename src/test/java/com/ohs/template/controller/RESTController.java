package com.ohs.template.controller;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class RESTController {


    public static class UserView{
        public String name;
        public int age;
    }
/*
    @RestController를 사용하면 Spring Framework가 반환하는 List<User> 객체는 자동으로 JSON 형식으로 변환됩니다.
    이 변환 과정은 일반적으로 Jackson 라이브러리를 사용해 수행됩니다.*/
    @GetMapping
    public List<UserView> getAllUsers() {
        // 로직
        return null;
    }

    @GetMapping("/{id}")
    public UserView getUserById(@PathVariable Long id) {
        // 로직
        return null;
    }

    @PostMapping
    public UserView createUser(@RequestBody UserView user) {
        // 로직
        return null;
    }

    @PutMapping("/{id}")
    public UserView updateUser(@PathVariable Long id, @RequestBody UserView user) {
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        // 로직
    }

}
