package com.ohs.monolithic.utils;


import org.springframework.test.context.ActiveProfiles;





// flyway, mysql 종속적인 쿼리를 테스트할 목적.
// 로컬에 mysql 테스트 서버가 존재해야한다.
// 환경변수 DATABASE_SECONDARY_* 를 정의해야함. (application-testwithmysql 참고)
@ActiveProfiles("testwithmysql")
public class IntegrationTestWithMySQL extends IntegrationTestBase{
  @Override
  protected String getDatabaseType() {
    return "mysql";
  }
}
