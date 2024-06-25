package com.ohs.monolithic.common.configuration;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class FlywayConfig {
  //final Flyway flyway;

  @Value("${spring.flyway.custom.repair-on-start}")
  private boolean repairOnStart;

  @Bean
  public FlywayMigrationStrategy migrateStrategy() {
    return flyway -> {
      if(repairOnStart) flyway.repair();
      flyway.migrate();
    };
  }

}
