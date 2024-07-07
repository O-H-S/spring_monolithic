package com.ohs.monolithic.problem.repository;

import com.ohs.monolithic.problem.domain.Problem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long>, CustomProblemRepository {

  Problem findByPlatformAndPlatformId(String platform, String platformId);

  @Query("SELECT p FROM Problem p WHERE p.difficulty != null and p.level = null")
  List<Problem> findCandidateForLeveling();

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT p FROM Problem p WHERE p.id = :problemId")
  Optional<Problem> findProblemWithLock(@Param("problemId") Long problemId);

}
