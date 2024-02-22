package com.ohs.monolithic.user.controller;


import com.ohs.monolithic.user.dto.AccountResponse;
import com.ohs.monolithic.user.dto.AppUser;
import com.ohs.monolithic.user.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts/{id}")
public class AccountApiController {

  final private AccountService accountService;
  @PreAuthorize("isAuthenticated()")
  @PatchMapping
  public ResponseEntity<?> updatePartialAccount(@AuthenticationPrincipal AppUser user, @PathVariable Long id, @RequestBody Map<String, String> requestBody){
    AccountResponse result = accountService.updateAccount(id, requestBody, user);
    return ResponseEntity.ok().body(result);
  }

}
