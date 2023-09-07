package com.ohs.monolithic.board.manage;


import com.ohs.monolithic.board.Board;
import com.ohs.monolithic.board.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class BoardManageService {
    final BoardRepository bRepo;

    public void createBoard(String title, String desc){
        Board newBoard = new Board();
        newBoard.setTitle(title);
        newBoard.setCreateData(LocalDateTime.now());
        newBoard.setDescription(desc);
        bRepo.save(newBoard);

    }

    public List<Board> getBoards() {
        return bRepo.findAll();
    }


}
