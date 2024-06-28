package com.ohs.monolithic.problem.collect.service;

import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.common.exception.PermissionException;
import com.ohs.monolithic.problem.collect.domain.CollectProgress;
import com.ohs.monolithic.problem.collect.dto.CollectProgressResponse;
import com.ohs.monolithic.problem.collect.dto.CollectProgressUpdateForm;
import com.ohs.monolithic.problem.collect.dto.ProblemDto;
import com.ohs.monolithic.problem.collect.repository.CollectProgressRepository;
import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.repository.ProblemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemCollectService {
  final CollectProgressRepository collectProgressRepository;
  final ProblemRepository problemRepository;

  @Transactional(readOnly = true)
  public CollectProgressResponse getProgress(AppUser user, String target, int version) {
    if(!user.isAdmin()){
      throw new PermissionException("권한이 없습니다");
    }

    CollectProgress result = collectProgressRepository.findByPlatformAndCollectorVersion(target, version);
    if(result == null)
      throw new EntityNotFoundException("존재하지 않는 대상입니다");

    return CollectProgressResponse.of(result);
  }

  @Transactional
  public void updateProgressWindow(AppUser user, String target, int version, CollectProgressUpdateForm form){
    CollectProgress result = collectProgressRepository.findByPlatformAndCollectorVersion(target, version);
    if(result == null)
    {
      result = new CollectProgress();
      result.setPlatform(target);
      result.setCollectorVersion(version);
      result.setLastWindow(-1);
      result.setStartDate(LocalDateTime.now());
    }

    // window 업데이트를 점진적으로 진행하지 않으면, 예외 발생
    if(result.getLastWindow() + 1 < form.getTargetWindow())
      throw new RuntimeException("불가능한 window index 입니다.");


    // 이미 존재하는 데이터들을 한번에 불러오기 위한 전처리 과정.
    List<ProblemDto> problemDtoList = form.getProblemList();
    ArrayList<String> platforms = new ArrayList<>(problemDtoList.size());
    ArrayList<String> ids = new ArrayList<>(problemDtoList.size());
    for(ProblemDto dto : problemDtoList){
      platforms.add(dto.getPlatform());
      ids.add(dto.getPlatformId());
    }

    // problemRepository.bulkInsert 의 동작은 기존에 데이터가 존재할 시, 덮어씌우기 때문에, 기존 데이터 필드의 유지를 위해 로드하여 재가공한다.
    List<Problem> existingProblems = problemRepository.findProblemsWithLock(platforms, ids);
    Map<Pair<String, String>, Problem> temp = new HashMap<>(); // 효율적으로 처리하기 위함.
    for(Problem p : existingProblems){
      temp.put(Pair.of(p.getPlatform(), p.getPlatformId()), p);
    }

    // 기존 데이터 + 새롭게 입력된 데이터(dto) 와 조합하여 Entity 작성.
    List<Problem> problems = new ArrayList<>(problemDtoList.size());
    for(ProblemDto dto : problemDtoList){
      Problem oldProblem = temp.get(Pair.of(dto.getPlatform(), dto.getPlatformId()));

      String newTitle = dto.getTitle() != null ? dto.getTitle() : oldProblem != null ? oldProblem.getTitle() : null;

      String newDifficulty = dto.getDifficulty() != null ? dto.getDifficulty() : oldProblem != null ? oldProblem.getDifficulty() : null;

      String newLink = dto.getLink() != null ? dto.getLink() : oldProblem != null ? oldProblem.getLink() : "";

      LocalDateTime newFoundDate = oldProblem != null && oldProblem.getFoundDate() != null ? oldProblem.getFoundDate() : dto.getFoundDate();

      Problem newProblem = Problem.builder()
              .platform(dto.getPlatform())
              .platformId(dto.getPlatformId())
              .title(newTitle)
              .difficulty(newDifficulty)
              .link(newLink)
              .foundDate(newFoundDate)
              .postCount(oldProblem != null ?oldProblem.getPostCount() : 0)
              .version(dto.getCollectorVersion())
              .build();

      problems.add(newProblem);
    }

    // batch upsert 진행
    problemRepository.bulkInsert(problems);


    result.setLastWindow(form.getTargetWindow());
    collectProgressRepository.save(result);

    log.info("문제가 수집되었습니다 : " + target + " - " + form.getTargetWindow());
  }
}
