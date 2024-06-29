package com.ohs.monolithic.utils;

import com.nimbusds.jose.shaded.gson.Gson;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.auth.domain.LocalAppUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Tag("base")
@Tag("integrate")
@ActiveProfiles("test")
public class IntegrationTestBase {


  @Autowired
  protected MockMvc mockMvc;
  protected Gson gson; // json 직렬화,역직렬화


  @Autowired
  protected IntegrationTestHelper helper;




  @BeforeEach
  public void init() {
    gson = new Gson();
  }

  @AfterEach
  public void release(){
    helper.release();
  }


  public Account initSecurityUserAccount() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.getPrincipal() instanceof LocalAppUser) {
      LocalAppUser localAppUser = (LocalAppUser) authentication.getPrincipal();
      //helper.accountRepository.
      //helper.accountRepository.save(localAppUser.getAccount());
      Account newAccount = helper.accountService.createAsLocal(localAppUser.getNickname(), WithMockCustomUserContext.getAccount().getEmail(), localAppUser.getUsername(), localAppUser.getPassword());

      helper.localAccountService.reload(null);
      WithMockCustomUserContext.setAccount(newAccount);
      WithMockCustomUserContext.setAppUser( (LocalAppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
     // Account newAccount = helper.accountRepository.save(WithMockCustomUserContext.getAccount());

      return newAccount;
     // helper.accountRepository.flush();
    }
    return null;
  }


}
