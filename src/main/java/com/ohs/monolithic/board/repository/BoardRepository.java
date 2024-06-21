package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Integer>, CustomBoardRepository {

  Board findByTitle(String title);

}