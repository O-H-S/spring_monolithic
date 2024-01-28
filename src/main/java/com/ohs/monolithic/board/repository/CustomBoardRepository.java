package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.dto.BoardResponse;

import java.util.List;

public interface CustomBoardRepository  {
    List<BoardResponse> getAllBoards(boolean includeTitle, boolean includeDesc);
    void deleteBoard(Integer boardID);
}
