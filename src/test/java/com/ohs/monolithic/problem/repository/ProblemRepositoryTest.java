package com.ohs.monolithic.problem.repository;

import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.dto.ProblemPaginationDto;
import com.ohs.monolithic.utils.RepositoryTestWithMysql;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProblemRepositoryTest extends RepositoryTestWithMysql {
  @Autowired
  ProblemRepository problemRepository;

  @Test
  @DisplayName("전문검색, ngram == 2 (default)")
  public void selectAll() {

    Problem p0 = helper.establishProblem("baekjoon", "quiz title - abc");
    Problem p1 = helper.establishProblem("baekjoon", "quiz title - edfab");
    Problem p2 = helper.establishProblem("baekjoon", "quiz title - abced");
    Problem p3 = helper.establishProblem("baekjoon", "quiz title - 가나다");
    Problem p4 = helper.establishProblem("baekjoon", "quiz title - 라마바");
    Problem p5 = helper.establishProblem("baekjoon", "quiz title - 사아자가나");
    Problem p6 = helper.establishProblem("baekjoon", "quiz title - <[]>");

    // 전문(Fulltext) 인덱스는 커밋이 완료된 후에 업데이트 되므로, 커밋으로 테스트 트랜잭션을 종료한다.
    // RepositoryTestWithMysql의 aftereach에서 데이터베이스를 초기화하므로 다시 삭제할 필요는 없음.
    TestTransaction.flagForCommit();
    TestTransaction.end();


    Sort sort = Sort.by(Sort.Direction.DESC, "foundDate");
    Pageable pageable = PageRequest.of(0, 10,sort);


    Page<ProblemPaginationDto> result = problemRepository.selectByKeywords(pageable, null, "df");
    assertEquals(1, result.getTotalElements(), "검색 결과는 1개여야 합니다.");
    assertEquals(p1.getId(), result.getContent().get(0).getId(), "p1 문제만 유일하게 df 토큰을 생성합니다. ");

    Page<ProblemPaginationDto> result2 = problemRepository.selectByKeywords(pageable, null, "bc");
    assertEquals(2, result2.getTotalElements(), "검색 결과는 2개여야 합니다.");
    assertEquals(p2.getId(), result2.getContent().get(0).getId(), "p2가 나중에 추가 되었으므로, p0보다 앞에와야 합니다.");
    assertEquals(p0.getId(), result2.getContent().get(1).getId(), "p2가 나중에 추가 되었으므로, p0보다 앞에와야 합니다.");

     /*  불용어 활성화 시, 테스트.
      // 기본적으로 'a'는 default stopwords에 등록되어 있으므로, 검색 결과가 0개이다.
      Page<ProblemPaginationDto> result3 = problemRepository.selectAllByKeywords(pageable, null, "ab");
      assertEquals(0, result3.getTotalElements(), "검색 결과는 0개여야 합니다.");

      // fa의 경우도 마찬가지고, a가 포함되어있으므로 위와 같다.
      Page<ProblemPaginationDto> result4 = problemRepository.selectAllByKeywords(pageable, null, "fa");
      assertEquals(0, result4.getTotalElements(), "검색 결과는 0개여야 합니다.");

      // fab의 경우도 마찬가지고, a가 포함되어있으므로 위와 같다.
      Page<ProblemPaginationDto> result44 = problemRepository.selectAllByKeywords(pageable, null, "fab");
      assertEquals(0, result44.getTotalElements(), "검색 결과는 0개여야 합니다.");
     */

    // bcdf는 'bc' 'cd' 'df'를 OR 연산으로 찾게 되므로, score가 다르지만 3가지 경우를 포함한다.
    Page<ProblemPaginationDto> result5 = problemRepository.selectByKeywords(pageable, null, "bcdf");
    assertEquals(3, result5.getTotalElements(), "검색 결과는 3개여야 합니다.");

    /*기본적으로 ngram 파서는 알파벳, 숫자, 언더스코어(_)를 제외한 모든 문자를 구분자(word boundary)로 취급합니다.
    구분자로 취급되는 특수 문자는 토큰화 과정에서 제외됩니다.
            즉, 특수 문자는 기본적으로 인덱싱되지 않습니다.*/
    Page<ProblemPaginationDto> result6 = problemRepository.selectByKeywords(pageable, null, "<[");
    assertEquals(0, result6.getTotalElements(), "검색 결과는 0개여야 합니다.");

    Page<ProblemPaginationDto> result7 = problemRepository.selectByKeywords(pageable, null, "가나");
    assertEquals(2, result7.getTotalElements(), "검색 결과는 2개여야 합니다.");

    // Natural Language Mode에서는 와일드카드 사용 못함.
    Page<ProblemPaginationDto> result8 = problemRepository.selectByKeywords(pageable, null, "가*");
    assertEquals(0, result8.getTotalElements(), "검색 결과는 0개여야 합니다.");

    // Boolean Mode 에서도 한글자로는 불가능
    Page<ProblemPaginationDto> result9 = problemRepository.selectByKeywordsWithBooleanMode(pageable, null, "가");
    assertEquals(0, result9.getTotalElements(), "검색 결과는 0개여야 합니다.");

    // Boolean Mode 에서 와일드카드 가능
    Page<ProblemPaginationDto> result10 = problemRepository.selectByKeywordsWithBooleanMode(pageable, null, "가*");
    assertEquals(2, result10.getTotalElements(), "검색 결과는 2개여야 합니다.");

    //
    Page<ProblemPaginationDto> result11 = problemRepository.selectByKeywordsWithBooleanMode(pageable, null, "bcdf");
    assertEquals(0, result11.getTotalElements(), "검색 결과는 0개여야 합니다.");

    //
    Page<ProblemPaginationDto> result12 = problemRepository.selectByKeywordsWithBooleanMode(pageable, null, "가나다");
    assertEquals(1, result12.getTotalElements(), "검색 결과는 1개여야 합니다.");

    // Boolean Mode 에서 "abcd"와 "+ab +bc +cd"는 다른 결과이다. (후자가 더 넓은 범위)("abcd" = "+abcd")
  }


  @Test
  @DisplayName("플랫폼 필터 검색")
  public void selectByPlatforms() {
    Problem b1 = helper.establishProblem("baekjoon", "백준 문제 1");
    Problem s1 = helper.establishProblem("softeer", "소프티어 문제1");
    Problem b2 = helper.establishProblem("baekjoon", "백준 문제 2");
    Problem s2 = helper.establishProblem("softeer", "소프티어 문제2");
    Problem s3 = helper.establishProblem("softeer", "소프티어 문제3");
    Problem p1 = helper.establishProblem("programmers", "프로그래머스 문제1");
    Problem b3 = helper.establishProblem("baekjoon", "백준 문제 3");

    // 전문(Fulltext) 인덱스는 커밋이 완료된 후에 업데이트 되므로, 커밋으로 테스트 트랜잭션을 종료한다.
    // RepositoryTestWithMysql의 aftereach에서 데이터베이스를 초기화하므로 다시 삭제할 필요는 없음.
    TestTransaction.flagForCommit();
    TestTransaction.end();


    Sort sort = Sort.by(Sort.Direction.DESC, "foundDate");
    Pageable pageable = PageRequest.of(0, 10, sort);


    Page<ProblemPaginationDto> result = problemRepository.selectByPlatforms(pageable, null,  List.of("softeer"));
    assertEquals(3, result.getTotalElements(), "소프티어 필터 결과는 3개여야 합니다.");

    Page<ProblemPaginationDto> result2 = problemRepository.selectByPlatforms(pageable, null, List.of("programmers"));
    assertEquals(1, result2.getTotalElements(), "프로그래머스 필터 결과는 1개여야 합니다.");
    assertEquals(p1.getId(), result2.getContent().get(0).getId(), "프로그래머스 문제가 아닙니다.");

  }

  @Test
  @DisplayName("정렬 기준 변경")
  public void select_order(){
    helper.establishProblem("baekjoon", "문제 1").setLevel(2f);
    helper.establishProblem("baekjoon", "문제 2").setLevel(3f);
    helper.establishProblem("baekjoon", "문제 3").setLevel(1f);
    helper.establishProblem("baekjoon", "문제 4").setLevel(1.5f);
    helper.establishProblem("baekjoon", "문제 5").setLevel(null);

    TestTransaction.flagForCommit();
    TestTransaction.end();

    Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "foundDate"));
    Page<ProblemPaginationDto> result = problemRepository.selectAll(pageable, null);

    Pageable pageable2 = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "foundDate"));
    Page<ProblemPaginationDto> result2 = problemRepository.selectAll(pageable2, null);

    Pageable pageable3 = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "level"));
    Page<ProblemPaginationDto> result3 = problemRepository.selectAll(pageable3, null);
    assertThat(result3.getContent())
            .first().extracting(ProblemPaginationDto::getLevel).isEqualTo(3f);
    assertThat(result3.getContent())
            .last().extracting(ProblemPaginationDto::getLevel).isEqualTo(null); // null 값은 항상 뒷 순서 : orderSpecifier.nullsLast().

    Pageable pageable4 = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "level"));
    Page<ProblemPaginationDto> result4 = problemRepository.selectAll(pageable4, null);
    assertThat(result4.getContent())
            .first().extracting(ProblemPaginationDto::getLevel).isEqualTo(1f);
    assertThat(result4.getContent())
            .last().extracting(ProblemPaginationDto::getLevel).isEqualTo(null); // null 값은 항상 뒷 순서 : orderSpecifier.nullsLast().

  }


  @Test
  @DisplayName("난이도 범위 필터")
  public void select_levelRange(){
    helper.establishProblem("baekjoon", "문제 1").setLevel(2f);
    helper.establishProblem("baekjoon", "문제 2").setLevel(3f);
    helper.establishProblem("baekjoon", "문제 3").setLevel(1f);
    helper.establishProblem("baekjoon", "문제 4").setLevel(1.5f);
    helper.establishProblem("baekjoon", "문제 5").setLevel(null);

    TestTransaction.flagForCommit();
    TestTransaction.end();

    // null 값은 항상 포함하며, 양 쪽 경계는 inclusive 이다. (1, 1.5, 2, null 리턴)
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "foundDate"));
    Page<ProblemPaginationDto> result = problemRepository.selectByLevelRange(pageable, null, new Float[]{1f, 2f});
    assertEquals(4, result.getTotalElements(), "결과는 4개여야 합니다.");

    // null, 1 리턴
    Page<ProblemPaginationDto> result2 = problemRepository.selectByLevelRange(pageable, null, new Float[]{1f, 1f});
    assertEquals(2, result2.getTotalElements(), "결과는 2개여야 합니다.");

    // 범위 검증은 별도로 작성해야한다. (null만 리턴)
    Page<ProblemPaginationDto> result3 = problemRepository.selectByLevelRange(pageable, null, new Float[]{1f, 0f});
    assertEquals(1, result3.getTotalElements(), "결과는 1개여야 합니다.");
  }

 /* @Test
  @DisplayName("bulk insert")
  public void bulkinsert(){
    List<Problem> problems = List.of(
            Problem.builder().platform("baekjoon").platformId("1").build(),
            Problem.builder().build()
    );

    problemRepository.bulkInsert(problems);

  }*/

}
