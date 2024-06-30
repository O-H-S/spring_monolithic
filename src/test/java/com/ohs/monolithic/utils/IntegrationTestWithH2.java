package com.ohs.monolithic.utils;


import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("testwithh2")
public class IntegrationTestWithH2  extends IntegrationTestBase{
  @Override
  protected String getDatabaseType() {
    return "h2";
  }
}
