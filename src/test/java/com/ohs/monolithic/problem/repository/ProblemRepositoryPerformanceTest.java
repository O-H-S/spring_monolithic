package com.ohs.monolithic.problem.repository;


import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.utils.RepositoryTestWithMysql;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 어노테이션으로 로그 끄기 실패함;;
//@ActiveProfiles("logoff")
/*@TestPropertySource(properties = {
        "spring.jpa.show-sql=false",
        "logging.level.org.hibernate.SQL=OFF",
        "logging.level.org.hibernate.type.descriptor.sql.BasicBinder=OFF"
})*/
//@TestPropertySource(locations = "classpath:logoff.properties")
public class ProblemRepositoryPerformanceTest extends RepositoryTestWithMysql {
  @Autowired
  ProblemRepository problemRepository;
  int problemSize = 100;
  int iterationCount = 5;
  long globalOffset = 0;

  List<Long> times = new ArrayList<>(iterationCount);

  @Value("${spring.jpa.show-sql}")
  private boolean showSql;

  // 코드로 로그 끄기
  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.jpa.show-sql", () -> "false");
    registry.add("logging.level.org.hibernate.SQL", () -> "OFF");
    registry.add("logging.level.org.hibernate.type.descriptor.sql.BasicBinder", () -> "OFF");
  }
  @Test
  @DisplayName("saveAll 방법")
  public void method() {

    long duration = 0;
    for(int i = 0; i < iterationCount; i++) {
      List<Problem> problems = createNewProblems(problemSize);
      long startTime = System.nanoTime(); // 시작 시간 기록

      problemRepository.saveAll(problems);

      long endTime = System.nanoTime(); // 종료 시간 기록
      duration += endTime - startTime;
      times.add((endTime-startTime)/1_000_000);
    }
    System.out.println("%d개".formatted(problemSize));
    System.out.println(times.toString());
    System.out.println("평균 : " +duration/iterationCount /1_000_000 + "ms");
  }

  @Test
  @DisplayName("bulkInsert 방법")
  public void method2() {

    long duration = 0;
    for(int i = 0; i < iterationCount; i++) {
      List<Problem> problems = createNewProblems(problemSize);
      long startTime = System.nanoTime(); // 시작 시간 기록

      problemRepository.bulkInsert(problems);

      long endTime = System.nanoTime(); // 종료 시간 기록
      duration += endTime - startTime;
      times.add((endTime-startTime)/1_000_000);
    }

    System.out.println("%d개".formatted(problemSize));
    System.out.println(times.toString());
    System.out.println("평균 : " +duration/iterationCount /1_000_000 + "ms");
  }


  List<Problem> createNewProblems(int size){
    List<Problem> problems = new ArrayList<>(size);
    for(int i = 0; i < size; i++) {
      Problem newProblem = Problem.builder()
              .platform("beakjoon")
              .title("testTitle")
              .platformId("p" + globalOffset++)
              .version(0)
              .foundDate(LocalDateTime.now())
              .link("https://test.com")
              .build();
      problems.add(newProblem);
    }
    return problems;
  }

}
