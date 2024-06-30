package com.ohs.monolithic.utils;


import com.nimbusds.jose.shaded.gson.Gson;
import com.ohs.monolithic.account.service.OAuth2AccountService;
import com.ohs.monolithic.common.configuration.SecurityApiConfig;
import com.ohs.monolithic.common.configuration.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs
@EnableMethodSecurity(prePostEnabled = true)
@Import({SecurityConfig.class, SecurityApiConfig.class})
@Tag("base")
@Tag("unit")
public abstract class ControllerTestBase {

  @Autowired
  protected MockMvc mockMvc; // abstract를 사용하면, @autowired를 빈이 아니라도 사용 가능하다.

  @MockBean
  protected OAuth2AccountService accountService; // SecurityConfig가 필요로 함.
  protected Gson gson; // json 직렬화,역직렬화
  @BeforeEach
  void init() {
    gson = new Gson();
    beforeEach();
  }

  protected void beforeEach(){

  }

}
