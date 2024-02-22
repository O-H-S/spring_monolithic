package com.ohs.monolithic.account.repository;

import com.ohs.monolithic.account.domain.LocalCredential;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalCredentialRepository  extends JpaRepository<LocalCredential, Long> {

  @EntityGraph(attributePaths = {"account"})
  public Optional<LocalCredential> findByUsername(String username);
}
