package com.ohs.monolithic.utils;


import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("testwithh2")
public class RepositoryTestWithH2 extends RepositoryTestBase{
  @Override
  protected String getDatabaseType() {
    return "h2";
  }
}
