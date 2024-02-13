package com.ohs.monolithic.user.repository;

import com.ohs.monolithic.user.domain.Account;
import com.ohs.monolithic.user.domain.LocalCredential;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocalCredentialRepository  extends JpaRepository<LocalCredential, Long> {

  @EntityGraph(attributePaths = {"account"})
  public Optional<LocalCredential> findByUsername(String username);
}
