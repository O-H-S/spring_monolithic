package com.ohs.monolithic.problem.repository;

import com.ohs.monolithic.common.utils.BulkInsertableRepository;
import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.dto.ProblemPaginationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public interface CustomProblemRepository extends BulkInsertableRepository<Problem, Long> {

  Page<ProblemPaginationDto> selectAll(Pageable pageable, Long viewerId);
  Page<ProblemPaginationDto> selectByDetails(Pageable pageable, Long viewerId, String keywords, Boolean keywords_booleanMode, List<String> platforms, Float[] levelRange);
  Page<ProblemPaginationDto> selectByKeywords(Pageable pageable, Long viewerId, String keywords);
  Page<ProblemPaginationDto> selectByKeywordsWithBooleanMode(Pageable pageable, Long viewerId, String keywords);
  Page<ProblemPaginationDto> selectByPlatforms(Pageable pageable, Long viewerId, List<String> platforms);
  Page<ProblemPaginationDto> selectByLevelRange(Pageable pageable, Long viewerId, Float[] levelRange);


  List<Problem> findProblemsWithLock(ArrayList<String> platform, ArrayList<String> Ids);

}
