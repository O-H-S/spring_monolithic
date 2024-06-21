package com.ohs.monolithic.problem.repository;

import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.common.utils.BulkInsertableRepository;
import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.dto.ProblemPaginationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface CustomProblemRepository extends BulkInsertableRepository<Problem, Long> {

  Page<ProblemPaginationDto> selectAll(Pageable pageable, Long viewerId);
  List<Problem> findProblemsWithLock(ArrayList<String> platform, ArrayList<String> Ids);

}
