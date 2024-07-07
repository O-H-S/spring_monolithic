package com.ohs.monolithic.problem;


import com.ohs.monolithic.problem.collect.ProblemCollectManager;
import com.ohs.monolithic.problem.service.ProblemLevelService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProblemLevelUpdater implements ApplicationRunner {
  final ProblemLevelService problemLevelService;
  @Override
  public void run(ApplicationArguments args) {
    problemLevelService.updateLevels();
  }
  @PreDestroy
  public void onExit() {


  }
}
