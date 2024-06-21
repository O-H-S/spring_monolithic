package com.ohs.monolithic.problem.dto;


import com.ohs.monolithic.account.dto.AccountNotificationResponse;
import com.ohs.monolithic.problem.domain.ProblemPostNotification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openqa.selenium.devtools.v85.cast.Cast;

import java.lang.reflect.ReflectPermission;

@Setter
@Getter
@NoArgsConstructor
public class ProblemPostNotificationResponse extends AccountNotificationResponse {

  Long problemId;
  String problemName;
  Long postId;
  String postName;
  Boolean valid;
  public static ProblemPostNotificationResponse of(ProblemPostNotification source){
    ProblemPostNotificationResponse response = new ProblemPostNotificationResponse();

    response.setId(source.getId());
    response.type = "problemPost";
    response.setViewed(source.getViewed());
    response.setTimestamp(source.getTimestamp());


    // N+1 문제, of에서 관련된 인자를 직접 받도록 하기.
    // 방법1 ) builder에서 "초기화" 메서드 오버라이드하여, 한번에 관련 필드들만 불러오기.
    // 방법2 ) 캐시 사용 : 2차 캐시, redis 전역 캐시
    response.problemId = source.getProblem().getId();
    response.problemName = source.getProblem().getTitle();
    response.postId = source.getPost().getId();
    response.postName = source.getPost().getTitle();
    response.valid = source.getValid();


    return response;
  }

}
