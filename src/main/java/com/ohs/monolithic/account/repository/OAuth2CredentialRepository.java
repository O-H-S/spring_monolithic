package com.ohs.monolithic.account.repository;

import com.ohs.monolithic.account.domain.OAuth2Credential;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2CredentialRepository  extends JpaRepository<OAuth2Credential, Long> {

  @EntityGraph(attributePaths = {"account"})
  Optional<OAuth2Credential> findByProviderAndProviderId(String provider, String providerId);

}
