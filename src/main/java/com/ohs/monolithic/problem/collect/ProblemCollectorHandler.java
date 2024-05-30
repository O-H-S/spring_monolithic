package com.ohs.monolithic.problem.collect;

import com.ohs.monolithic.problem.domain.Problem;

import java.util.List;

public interface ProblemCollectorHandler {
  void onCollectProblem(List<Problem> problemList);

}
