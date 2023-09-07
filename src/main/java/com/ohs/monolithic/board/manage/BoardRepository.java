package com.ohs.monolithic.board.manage;

import com.ohs.monolithic.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Integer> {



}