package com.ohs.monolithic.utils;


import com.ohs.monolithic.common.configuration.QuerydslConfig;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Tag("base")
@Tag("integrate-limited")
@Import({QuerydslConfig.class, RepositoryTestHelper.class})
@ActiveProfiles("repositorytest")
public class RepositoryTestBase {

  @Autowired
  protected RepositoryTestHelper helper;

}
