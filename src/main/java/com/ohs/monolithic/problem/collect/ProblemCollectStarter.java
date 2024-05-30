package com.ohs.monolithic.problem.collect;


import com.ohs.monolithic.problem.collect.ProblemCollectManager;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProblemCollectStarter implements ApplicationRunner {

  final ProblemCollectManager manager;
  @Override
  public void run(ApplicationArguments args) {
    try{
      System.out.println("크롤러 매니저 실행 중.");
      manager.start();
      //System.out.println("크롤러 매니저 실행 완료");
    }catch (Exception e){
      System.out.println("크롤러 매니저 실행 실패");
      e.printStackTrace();
    }
  }
  @PreDestroy
  public void onExit() {


  }
}
