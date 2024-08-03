package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer>, CustomBoardRepository {

  Board findByTitleAndDeletedFalse(String title);
  List<Board> findAllByDeletedFalse();
  Optional<Board> findByIdAndDeleted(Integer id, Boolean deleted);

}