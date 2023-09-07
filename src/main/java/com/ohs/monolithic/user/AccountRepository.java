package com.ohs.monolithic.user;


import com.ohs.monolithic.board.Comment;
import com.ohs.monolithic.board.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByusername(String username);

}