package com.ohs.monolithic.problem.repository;


import com.ohs.monolithic.account.domain.Account;
import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.domain.ProblemBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemBookmarkRepository extends JpaRepository<ProblemBookmark, Long> {
  Optional<ProblemBookmark> findByAccountAndProblem(Account account, Problem problem);
}
