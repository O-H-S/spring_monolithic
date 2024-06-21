package com.ohs.monolithic.problem.collect.collector;


import com.ohs.monolithic.problem.collect.ProblemCollectManager;
import com.ohs.monolithic.problem.collect.ProblemCollectorHandler;
import com.ohs.monolithic.problem.collect.domain.CollectProgress;
import com.ohs.monolithic.problem.domain.Problem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;

import java.util.List;

@Deprecated
@RequiredArgsConstructor
public abstract class ProblemCollector {
  @Getter
  protected WebDriver driver;
  final ProblemCollectorHandler handler;
  ProblemCollectManager manager;

  public int version = 0;
  public String platform;
  Integer cachedWindowSize;
  Integer cachedWindowsCount;



  public void start(ProblemCollectManager manager, WebDriver driver){
    this.manager = manager;
    this.driver = driver;
    //driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

    validatePage();

    CollectProgress lastProgress = manager.getProgress(this);
    int startWindowId = lastProgress.getLastWindow() + 1;
    int maxId = getWindowsCount()-1;
    if(maxId < startWindowId)
      startWindowId = maxId;
    for(int i = startWindowId; i <= maxId; i++){
      System.out.println(platform+ ") global collecting : " + i + "/" + maxId);
      List<Problem> problems = getProblemsFromWindow(i);
      lastProgress.setLastWindow(i);
      manager.insertProblems(problems, lastProgress);
    }

    // 지

  }


  protected void sleep(long time) {
    try {
      Thread.sleep(time);
    }catch (Exception e){
      throw new RuntimeException(e);
    }
  }
  protected void loadUrl(String url, long waitTime){
    if(!driver.getCurrentUrl().equals(url)) {
      driver.get(url);
      sleep(waitTime);
    }
  }

  protected abstract void validatePage(); // 대상 사이트가 기대하는 페이지 형식을 가지는지 검증함.
  protected abstract int getWindowSize();
  protected abstract int getWindowsCount();
  protected abstract List<Problem> getProblemsFromWindow(int windowId);

  // global 탐색을 끝내고 난 뒤, 지속적으로 최신 문제들을 가져오는 메소드
  // protected List<Problem> getRecentProblems(){ return null;}

}
