package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.utils.JdbcOperationsRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomBoardRepository  {
    List<BoardResponse> getAllBoards(boolean includeTitle, boolean includeDesc);
}
