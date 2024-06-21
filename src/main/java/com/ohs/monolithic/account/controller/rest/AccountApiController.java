package com.ohs.monolithic.account.controller.rest;


import com.ohs.monolithic.account.dto.*;
import com.ohs.monolithic.account.service.AccountNotificationService;
import com.ohs.monolithic.account.service.AccountService;
import com.ohs.monolithic.account.service.ProfileImageService;
import com.ohs.monolithic.auth.domain.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountApiController {

  final private AccountService accountService;
  final private AccountNotificationService notificationService;
  final private ProfileImageService profileImageService;

  //@PreAuthorize("!isAuthenticated()")
  @PostMapping("/")
  public ResponseEntity<?> createAccountData(@Valid @ModelAttribute AccountCreateForm form){
    AccountResponse result = accountService.createAccount(form);
    return ResponseEntity.ok().body(new AccountCreationResponse(result));
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}")
  public ResponseEntity<?> getAccountData(@AuthenticationPrincipal AppUser user, @PathVariable Long id){
    AccountResponse result = accountService.getAccount(id, user);
    return ResponseEntity.ok().body(result);
  }


  @PreAuthorize("isAuthenticated()")
  @GetMapping("/me")
  public ResponseEntity<?> getAccountDataMine(@AuthenticationPrincipal AppUser user){
    AccountResponse result = accountService.getAccount( user != null ?user.getAccountId() : null, user);
    return ResponseEntity.ok().body(result);
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping("/me/admin")
  public ResponseEntity<?> updateRoleMine(@AuthenticationPrincipal AppUser user, @RequestBody Map<String, String> request){
    accountService.upgradeToAdmin(user, request.get("adminKey"));
    return ResponseEntity.ok().build();
    //return updatePartialAccount(user, user.getAccountId(), requestBody);
  }

  @PreAuthorize("isAuthenticated()")
  @PatchMapping("/me")
  public ResponseEntity<?> updatePartialAccountMine(@AuthenticationPrincipal AppUser user, @RequestBody Map<String, String> requestBody){
    return updatePartialAccount(user, user.getAccountId(), requestBody);
  }


  @PreAuthorize("isAuthenticated()")
  @PatchMapping("/{id}")
  public ResponseEntity<?> updatePartialAccount(@AuthenticationPrincipal AppUser user, @PathVariable Long id, @RequestBody Map<String, String> requestBody){
    AccountResponse result = accountService.updateAccount(id, requestBody, user);
    return ResponseEntity.ok().body(result);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/me/profileUploadUrl")
  public ResponseEntity<?> getProfileUploadUrlMine(@AuthenticationPrincipal AppUser user, @ModelAttribute ProfileImageUploadDto.Request request){

    return getProfileUploadUrl(user, user.getAccountId(), request);
  }


  @PreAuthorize("isAuthenticated()")
  @GetMapping("/{id}/profileUploadUrl")
  public ResponseEntity<?> getProfileUploadUrl(@AuthenticationPrincipal AppUser user, @PathVariable Long id, @ModelAttribute ProfileImageUploadDto.Request request){

    ProfileImageUploadDto.Response result = profileImageService.getPresignedUrl(id, request, user);
    return ResponseEntity.ok().body(result);
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/me/notifications")
  public ResponseEntity<?> getNotificationsMine(@AuthenticationPrincipal AppUser user, @RequestParam(value = "lastDatetime", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastDateTime ) {
    //List<AccountNotificationResponse> result = notificationService.getNotifications(user, lastDateTime);
    AccountNotificationPaginationResponse response = notificationService.getNotifications(user, lastDateTime);

    return ResponseEntity.ok().body(response);
  }

/*  @PostMapping("/login")
  public ResponseEntity<?> successLogin(){

    return ResponseEntity.ok().build();
  }*/

}
