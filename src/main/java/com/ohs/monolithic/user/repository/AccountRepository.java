package com.ohs.monolithic.user.repository;


import com.ohs.monolithic.user.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

}