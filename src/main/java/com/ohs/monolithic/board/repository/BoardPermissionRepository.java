package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.BoardPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardPermissionRepository extends JpaRepository<BoardPermission, Long> {
  BoardPermission findByBoardAndNameAndValue(Board board, String name, String value);
  BoardPermission findByBoardAndName(Board board, String name);

  List<BoardPermission> findByBoardAndNameLike(Board board, String name);

}
