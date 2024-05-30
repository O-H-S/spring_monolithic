package com.ohs.monolithic.problem.collect.collector;

import com.ohs.monolithic.problem.collect.ProblemCollectorHandler;
import com.ohs.monolithic.problem.domain.Problem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProgrammersCollector extends ProblemCollector {

  private int cachedLastPage;
  public ProgrammersCollector(ProblemCollectorHandler handler) {
    super(handler);

    version = 1;
    platform = "programmers";
  }

  @Override
  protected void validatePage() {
    cachedLastPage = getWindowsCount();
    //getProblemsFromWindow(0);
    //throw new RuntimeException("abc");
  }

  @Override
  protected int getWindowSize() {
    //loadUrl("https://school.programmers.co.kr/learn/challenges?order=recent", 2000);


    return 20;
  }

  @Override
  protected int getWindowsCount() {
    // 임의의 큰 숫자 페이지로 이동하여, 수정된 url 검사하는 방법.
    loadUrl("https://school.programmers.co.kr/learn/challenges?order=recent&page=1000", 10000);

    UriComponents uriComponents = UriComponentsBuilder.fromUriString(driver.getCurrentUrl()).build();

    String pageValue = uriComponents.getQueryParams().getFirst("page");
    int counts = Integer.parseInt(pageValue);
    if(counts <= 5)
      throw new RuntimeException("ProgrammersCollector.getWindowsCount : 유효하지 않은 숫자입니다.");
    return counts;
  }

  @Override
  protected List<Problem> getProblemsFromWindow(int windowId) {
    loadUrl("https://school.programmers.co.kr/learn/challenges?order=recent&page=%d".formatted(cachedLastPage-windowId), 10000);

    WebElement tbodyElement = driver.findElement(By.cssSelector("table.ChallengesTablestyle__Table-sc-wt0ety-4 > tbody"));

    List<Problem> problemList = new ArrayList<>();

    List<WebElement> rows = tbodyElement.findElements(By.cssSelector("tr"));
    for (int i = rows.size() - 1; i >= 0; i--) {
      WebElement row = rows.get(i);
      List<WebElement> cells = row.findElements(By.tagName("td"));
      WebElement secondCellsDetail = cells.get(1).findElement(By.cssSelector("div.bookmark > a"));

      String title = secondCellsDetail.getText();
      String link = secondCellsDetail.getAttribute("href");
      String id = link.substring(link.lastIndexOf('/')+1);
      String difficulty = cells.get(2).findElement(By.cssSelector("span")).getAttribute("class");

      Problem problem = Problem.builder()
              .platform(platform)
              .platformId(id)
              .title(title)
              .difficulty(difficulty)
              .foundDate(LocalDateTime.now())
              .link(link)
              .version(version)
              .build();
      problemList.add(problem);
    }
    return problemList;
  }


}
