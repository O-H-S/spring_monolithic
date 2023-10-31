package com.ohs.monolithic.board.manage;


import com.ohs.monolithic.board.Board;
import com.ohs.monolithic.board.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


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

    public Board getBoardByID(Integer id){

        Optional<Board> boardOp = bRepo.findById(id);
        return boardOp.get();
    }

    public void save(Board board){
        bRepo.save(board);
    }


}
