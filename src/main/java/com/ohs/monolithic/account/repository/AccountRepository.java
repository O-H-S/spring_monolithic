package com.ohs.monolithic.account.repository;


import com.ohs.monolithic.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}