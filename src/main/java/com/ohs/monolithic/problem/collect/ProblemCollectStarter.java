package com.ohs.monolithic.problem.collect;


import com.ohs.monolithic.problem.collect.ProblemCollectManager;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Deprecated
public class ProblemCollectStarter implements ApplicationRunner {

  final ProblemCollectManager manager;
  @Override
  public void run(ApplicationArguments args) {
    /*try{
      log.info("크롤러 매니저 실행 중..");
      manager.start();
      //System.out.println("크롤러 매니저 실행 완료");
    }catch (Exception e){
      log.error("크롤러 매니저 실행 중 예외 발생: {}", e.getMessage(), e);
    }*/
  }
  @PreDestroy
  public void onExit() {


  }
}
