package com.ohs.monolithic.problem.collect.repository;

import com.ohs.monolithic.problem.collect.domain.CollectProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectProgressRepository extends JpaRepository<CollectProgress, Integer>{
  CollectProgress findByPlatformAndCollectorVersion(String platform, Integer collectorVersion);

}