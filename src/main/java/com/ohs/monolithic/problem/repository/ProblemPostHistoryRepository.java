package com.ohs.monolithic.problem.repository;

import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.domain.ProblemPostHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProblemPostHistoryRepository extends JpaRepository<ProblemPostHistory, Long> {

  @Query("SELECT PH FROM ProblemPostHistory PH " +
          "JOIN ProblemBookmark PB ON PB.account.id = :accountId " +
          "AND PB.problem = PH.problem " +
          "AND PH.createDate > :lastDatetime")
  List<ProblemPostHistory> findByAccountIdAndLastDatetime(
          @Param("accountId") Long accountId,
          @Param("lastDatetime") LocalDateTime lastDatetime
  );

}
