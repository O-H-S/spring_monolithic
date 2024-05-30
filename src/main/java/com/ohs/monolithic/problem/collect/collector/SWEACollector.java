package com.ohs.monolithic.problem.collect.collector;

import com.ohs.monolithic.problem.collect.ProblemCollectorHandler;
import com.ohs.monolithic.problem.domain.Problem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SWEACollector extends ProblemCollector {

  private int cachedLastPage;
  public SWEACollector(ProblemCollectorHandler handler) {
    super( handler);

    version = 0;
    platform = "swea";
  }

  @Override
  protected void validatePage() {
    cachedLastPage = getWindowsCount();
    //getProblemsFromWindow(0);
    //throw new RuntimeException("abc");
  }

  @Override
  protected int getWindowSize() {

    return 30;
  }

  @Override
  protected int getWindowsCount() {
    // 임의의 큰 숫자 페이지로 이동하여, 수정된 url 검사하는 방법.
    loadUrl("https://swexpertacademy.com/main/code/problem/problemList.do?contestProbId=&categoryId=&categoryType=&problemTitle=&orderBy=FIRST_REG_DATETIME&selectCodeLang=ALL&select-1=&pageSize=30&pageIndex=1", 10000);


    WebElement paginationParent = driver.findElement(By.cssSelector("ul.pagination"));
    List<WebElement> pages = paginationParent.findElements(By.cssSelector("li.page-item"));

    WebElement lastPage = pages.get(pages.size()-1).findElement(By.cssSelector("a.page-link"));

    String href = lastPage.getAttribute("href");
    String dataLabel = "pageIndex.value=";
    int dataPos = href.indexOf(dataLabel);
    String countString = "";
    for(int i = dataPos + dataLabel.length(); i < href.length(); i++ ){
      char c = href.charAt(i);
      if(c == ';')
        break;
      countString = countString.concat(String.valueOf(c));
    }


    int counts = Integer.parseInt(countString);
    if(counts <= 5)
      throw new RuntimeException("ProgrammersCollector.getWindowsCount : 유효하지 않은 숫자입니다.");
    return counts;
  }
  @Override
  protected List<Problem> getProblemsFromWindow(int windowId) {
    loadUrl("https://swexpertacademy.com/main/code/problem/problemList.do?contestProbId=&categoryId=&categoryType=&problemTitle=&orderBy=FIRST_REG_DATETIME&selectCodeLang=ALL&select-1=&pageSize=30&pageIndex=%d".formatted(cachedLastPage-windowId), 10000);

    List<Problem> problemList = new ArrayList<>();
    WebElement parent = driver.findElement(By.cssSelector("div.problem-list > div.widget-list"));
    List<WebElement> rows = parent.findElements(By.cssSelector("div.widget-box-sub"));
    for (int i = rows.size() - 1; i >= 0; i--) {
      WebElement row = rows.get(i);

      String title = row.findElement(By.cssSelector("span.week_text > a")).getText();
      title = title.replaceAll("&nbsp;", ""); // HTML 공백 엔티티를 일반 공백으로 치환
      title = title.replaceAll("\\[\\d+\\]", ""); // 대괄호 안의 텍스트 제거
      // 공백 정리하기 (연속된 공백을 하나의 공백으로 치환)
      title = title.trim().replaceAll(" +", " ");

      String codeFunc = row.findElement(By.cssSelector("span.week_text > a")).getAttribute("onclick");
      String code = codeFunc.substring(codeFunc.indexOf("('")+2, codeFunc.indexOf("')"));

      String link = "https://swexpertacademy.com/main/code/problem/problemDetail.do?contestProbId=" + code + "&categoryId=" + code + "&categoryType=CODE";

      String id = row.findElement(By.cssSelector("span.week_num")).getText();
      id = id.substring(0, id.length()-1);

      String difficulty = "";
      try{
        difficulty = row.findElement(By.cssSelector("div.widget-toolbar-sub > span.badge")).getText();
      }catch (Exception e){
        difficulty = "";
      }

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
