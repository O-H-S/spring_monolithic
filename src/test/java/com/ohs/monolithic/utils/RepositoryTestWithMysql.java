package com.ohs.monolithic.utils;

import com.ohs.monolithic.common.configuration.FlywayConfig;
import com.ohs.monolithic.problem.repository.ProblemRepositoryTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("testwithmysql")
//@Import(FlywayConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // @DataJpaTest의 인메모리 데이터베이스 기본 동작을 제어함.
@Sql(scripts = "classpath:data.sql") // @DataJpaTest는 기본적으로 schema.sql과 data.sql을 자동으로 실행하지 않습니다.
public class RepositoryTestWithMysql extends RepositoryTestBase{
  @Override
  protected String getDatabaseType() {
    return "mysql";
  }

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @AfterEach
  @Transactional
  public void cleanUp() {
    //entityManager.
    List<String> tables = jdbcTemplate.queryForList(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = 'ohsite'",
            String.class);

    tables.remove("flyway_schema_history");

    jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
    for (String table : tables) {
      jdbcTemplate.execute("TRUNCATE TABLE " + table);
    }
    jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
  }
}
