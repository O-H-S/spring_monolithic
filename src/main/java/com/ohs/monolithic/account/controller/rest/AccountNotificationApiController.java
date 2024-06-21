package com.ohs.monolithic.account.controller.rest;


import com.ohs.monolithic.account.service.AccountNotificationService;
import com.ohs.monolithic.auth.domain.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class AccountNotificationApiController {
  final private AccountNotificationService notificationService;
  @PreAuthorize("isAuthenticated()")
  @PatchMapping("/{id}/viewed")
  public ResponseEntity<?> patchNotificationViewed(@AuthenticationPrincipal AppUser user, @PathVariable Long id){
    notificationService.markNotificationAsViewed(id);
    return ResponseEntity.ok().build();
  }

}
