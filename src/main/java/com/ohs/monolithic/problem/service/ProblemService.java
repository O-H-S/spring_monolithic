package com.ohs.monolithic.problem.service;

import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.dto.ProblemPaginationDto;
import com.ohs.monolithic.problem.repository.ProblemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemService {
  final private ProblemRepository problemRepository;




  @Transactional(readOnly = true)
  public ProblemPaginationDto getProblemReadOnly(Long id){
    Optional<Problem> problemOp = problemRepository.findById(id);
    if(problemOp.isEmpty())
      throw new EntityNotFoundException("Problem not found");

    return ProblemPaginationDto.of( problemOp.get(), null);
  }



}
