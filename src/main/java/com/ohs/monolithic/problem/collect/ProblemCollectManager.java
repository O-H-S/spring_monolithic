package com.ohs.monolithic.problem.collect;


import com.ohs.monolithic.problem.collect.collector.*;
import com.ohs.monolithic.problem.collect.domain.CollectProgress;
import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.collect.repository.CollectProgressRepository;
import com.ohs.monolithic.problem.repository.ProblemRepository;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;


@Deprecated
//@Service
@RequiredArgsConstructor
public class ProblemCollectManager {
  final CollectProgressRepository collectProgressRepository;
  final ProblemRepository problemRepository;
  final ApplicationContext applicationContext;


  ProblemCollectManager self;

  List<ProblemCollector> collectorList = new ArrayList<>();

  @PostConstruct
  public void init(){
    System.setProperty("webdriver.chrome.whitelistedIps", "");
    //WebDriverManager.chromedriver().docker
    WebDriverManager.chromedriver().setup();
    collectorList.add(new BaekjoonCollector(null));
    collectorList.add(new ProgrammersCollector(null));
    collectorList.add(new SofteerCollector(null));
    collectorList.add(new SWEACollector(null));


  }

  WebDriver createDriver(){
    // 크롬 옵션 객체 생성
    ChromeOptions options = new ChromeOptions();
    options.addArguments("start-maximized"); // https://stackoverflow.com/a/26283818/1689770
    //options.addArguments("enable-automation"); // https://stackoverflow.com/a/43840128/1689770

    //options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
    //options.setExperimentalOption("useAutomationExtension", false);
    options.addArguments("--verbose"); // 로깅
    options.addArguments("--no-zygote"); // 좀비 프로세스 관련 : https://dev.to/styt/resolving-seleniums-zombie-process-issue-pak
    //options.addArguments("--single-process"); // https://github.com/puppeteer/puppeteer/issues/1825
    options.addArguments("--headless"); // only if you are ACTUALLY running headless
    options.addArguments("--no-sandbox"); //https://stackoverflow.com/a/50725918/1689770
    options.addArguments("--disable-dev-shm-usage");  //https://stackoverflow.com/a/50725918/1689770
    options.addArguments("--disable-browser-side-navigation"); //https://stackoverflow.com/a/49123152/1689770
    options.addArguments("disable-infobars"); // disabling infobars
    options.addArguments("--disable-extensions"); // disabling extensions
    options.addArguments("--disable-gpu"); //https://stackoverflow.com/questions/51959986/how-to-solve-selenium-chromedriver-timed-out-receiving-message-from-renderer-exc
    options.addArguments("--whitelisted-ips=''");
    options.addArguments("--remote-allow-origins=*");
    options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");

    // 아래 전략과 함께 명시적 대기 사용하는 방법도 있음.
    // options.setPageLoadStrategy(PageLoadStrategy.NONE);
    //
    WebDriver driver = new ChromeDriver(options);
    return driver;
  }

  @Transactional(readOnly = true)
  public CollectProgress getProgress(ProblemCollector collector){
    return getProgress(collector.platform, collector.version);
  }

  @Transactional(readOnly = true)
  public CollectProgress getProgress(String target, int version){
    CollectProgress result = collectProgressRepository.findByPlatformAndCollectorVersion(target, version);
    if(result == null)
    {
      result = new CollectProgress();
      result.setPlatform(target);
      result.setCollectorVersion(version);
      result.setLastWindow(-1);
      result.setStartDate(LocalDateTime.now());

    }
    return result;
  }

  @Transactional
  public void insertProblems(List<Problem> problems, CollectProgress progress){
    for(Problem problem : problems){
      Problem existingProblem = problemRepository.findByPlatformAndPlatformId(problem.getPlatform(), problem.getPlatformId());

      if(existingProblem == null ) {
        problemRepository.save(problem);
        continue;
      }
      if(existingProblem.getCollectorVersion() < problem.getCollectorVersion()){
        problemRepository.delete(existingProblem);
        problemRepository.flush();
        problemRepository.save(problem);
      }
    }

    collectProgressRepository.save(progress);
  }

  @Transactional
  public void modifyProblems(List<Problem> problems, CollectProgress progress, BiConsumer<Problem, Problem> modifier){
    for(Problem problem : problems){
      Problem existingProblem = problemRepository.findByPlatformAndPlatformId(problem.getPlatform(), problem.getPlatformId());

      if(existingProblem == null ) {
        continue;
      }
      if(existingProblem.getCollectorVersion() <= problem.getCollectorVersion()){
        modifier.accept(existingProblem, problem);
        problemRepository.save(existingProblem);
      }
    }

    collectProgressRepository.save(progress);
  }

  @Async
  public void start(){

    self = applicationContext.getBean(ProblemCollectManager.class);
    WebDriver reuseDriver = createDriver();
    try {
      while (true) {
        for (ProblemCollector collector : collectorList) {
          try {
            collector.start(self, reuseDriver);
          } catch(Exception e){
            System.out.println("크롤링 예외 inner : " + e.toString());
            e.printStackTrace();
          }
          finally {
            /*reuseDriver.manage().deleteAllCookies();

            // 로컬 및 세션 스토리지 클리어
            JavascriptExecutor js = (JavascriptExecutor) reuseDriver;
            js.executeScript("window.localStorage.clear();");
            js.executeScript("window.sessionStorage.clear();");
            // 빈 페이지로 이동
            reuseDriver.get("about:blank");*/

          }
          //self.startInner(collector); 비동기로 실행하기.
        }
        try {
          reuseDriver.close();
        }catch (Exception e)
        {
          System.out.println("driver close 예외 : " + e.toString());
          e.printStackTrace();
        }finally {
          reuseDriver.quit();
        }
        //reuseDriver
        Thread.sleep(1000 * 60 * 10);
        //System.out.println("driver 초기화 :" + collectorList.size());
        reuseDriver = createDriver();
      }
    }catch(Exception e){
      System.out.println("크롤링 루프 예외");
      e.printStackTrace();
    }finally {
      reuseDriver.quit();
    }
  }
  @Async
  void startInner(ProblemCollector collector){
   /* try{
      collector.start(self);
    }finally {
      collector.getDriver().quit();
    }*/

  }



}
