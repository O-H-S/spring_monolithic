package com.ohs.monolithic;


import com.ohs.monolithic.user.dto.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class MyPageController {

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/mypage")
  public String showMyPage(@AuthenticationPrincipal AppUser user, Model model){

    model.addAttribute("myAccount", user.getAccount());
    return "mypage";
  }
}
