package com.ohs.monolithic.problem.collect.collector;

import com.ohs.monolithic.problem.collect.ProblemCollectManager;
import com.ohs.monolithic.problem.collect.ProblemCollectorHandler;
import com.ohs.monolithic.problem.collect.domain.CollectProgress;
import com.ohs.monolithic.problem.domain.Problem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Deprecated
public class BaekjoonCollector extends ProblemCollector {
  int detail_maxIterCount = 10;
  public BaekjoonCollector( ProblemCollectorHandler handler) {
    super(handler);

    version = 0;
    platform = "baekjoon";
  }

  @Override
  public void start(ProblemCollectManager manager, WebDriver driver){
    super.start(manager, driver);

    CollectProgress lastProgress = manager.getProgress("baekjoon_detail", version);
    int startWindowId = lastProgress.getLastWindow() + 1;
    int maxId = getWindowsCount_Detail()-1;
    if(maxId < startWindowId)
      startWindowId = maxId;
    for(int i = startWindowId, iter = 0; iter < detail_maxIterCount && i <= maxId; i++, iter++){
      System.out.println(platform+ ") detail collecting : " + i + "/" + maxId);
      List<Problem> problems = getProblemsFromWindow_Detail(i);
      lastProgress.setLastWindow(i);
      manager.modifyProblems(problems, lastProgress, (origin, newProblem)->{
        origin.setDifficulty(newProblem.getDifficulty());
      });
    }
  }


  @Override
  protected void validatePage() {

  }

  @Override
  protected int getWindowSize() {
    loadUrl("https://www.acmicpc.net/problemset/1", 2000);

    WebElement element = driver.findElement(By.id("problemset"));
    // 해당 요소의 자식으로 있는 tbody 찾기
    WebElement tbody = element.findElement(By.tagName("tbody"));
    // tbody의 모든 직접적인 자식 요소 찾기
    List<WebElement> rows = tbody.findElements(By.cssSelector("tr"));
    int size = rows.size();
    if(size <= 3)
      throw new RuntimeException("BaekjoonCollector.getWindowSize : 유효하지 않은 숫자입니다.");
    return size;
  }

  @Override
  protected int getWindowsCount() {
    loadUrl("https://www.acmicpc.net/problemset/1", 5000);
    WebElement paginationElement = driver.findElement(By.cssSelector("div.col-md-12 > div.text-center > ul.pagination"));
    List<WebElement> rows = paginationElement.findElements(By.cssSelector("li"));
    int counts = rows.size();
    if(counts <= 5)
      throw new RuntimeException("BaekjoonCollector.getWindowsCount : 유효하지 않은 숫자입니다.");
    return counts;
  }



  @Override
  protected List<Problem> getProblemsFromWindow(int windowId) {
    loadUrl("https://www.acmicpc.net/problemset/%d".formatted(windowId+1), 3000);

    List<Problem> problemList = new ArrayList<>();

    WebElement element = driver.findElement(By.id("problemset"));
    // 해당 요소의 자식으로 있는 tbody 찾기
    WebElement tbody = element.findElement(By.tagName("tbody"));
    // tbody의 모든 직접적인 자식 요소 찾기
    List<WebElement> rows = tbody.findElements(By.cssSelector("tr"));
    for(WebElement row : rows){
      List<WebElement> cells = row.findElements(By.tagName("td"));
      WebElement secondCellsDetail = cells.get(1).findElement(By.tagName("a"));
      String id = cells.get(0).getText();
      String title = secondCellsDetail.getText();
      String link = secondCellsDetail.getAttribute("href");

      Problem problem = Problem.builder()
              .platform(platform)
              .platformId(id)
              .title(title)
              .difficulty(null)
              .foundDate(LocalDateTime.now())
              .link(link)
              .version(version)
              .build();
      problemList.add(problem);
    }

    return problemList;
  }
  // ========================== 디테일 크롤링 관련 ==============================
  int getWindowsCount_Detail() {
    loadUrl("https://solved.ac/problems/all", 5000);
    WebElement paginationElement = driver.findElement(By.cssSelector("div.css-18lc7iz"));
    List<WebElement> rows = paginationElement.findElements(By.cssSelector("a"));
    WebElement lastPageButton = rows.get(rows.size()-1);
    int counts = Integer.parseInt(lastPageButton.getText());
    if(counts <= 5)
      throw new RuntimeException("BaekjoonCollector.getWindowsCount_Detail : 유효하지 않은 숫자입니다.");
    return counts;
  }

  List<Problem> getProblemsFromWindow_Detail(int windowId) {
    loadUrl("https://solved.ac/problems/all?page=%d".formatted(windowId+1), 10000);

    List<Problem> problemList = new ArrayList<>();

    WebElement parent = driver.findElement(By.cssSelector("table.css-a651il > tbody.css-1d9xc1d"));
    List<WebElement> rows = parent.findElements(By.cssSelector("tr"));
    for(WebElement row : rows){
      List<WebElement> cells = row.findElements(By.tagName("td"));
      WebElement mark = cells.get(0).findElement(By.cssSelector("a.css-q9j30p > img"));
      String id = cells.get(0).findElement(By.cssSelector("a.css-q9j30p")).getText();

      Problem problem = Problem.builder()
              .platform(platform)
              .platformId(id)
              .difficulty(mark.getAttribute("alt"))
              .version(version)
              .build();
      problemList.add(problem);

    }

    return problemList;
  }

}
