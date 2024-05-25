package com.ohs.monolithic.problem.service;


import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.problem.dto.ProblemPaginationDto;
import com.ohs.monolithic.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemPaginationService {
  final ProblemRepository problemRepository;

  @Transactional(readOnly = true)
  public Page<ProblemPaginationDto> getList(int page, Integer pageSize, AppUser user) {

    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("foundDate"));
    Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
    return this.problemRepository.selectAll(pageable, user != null? user.getAccountId(): null);
  }

}
