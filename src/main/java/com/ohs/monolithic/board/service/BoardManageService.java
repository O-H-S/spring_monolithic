package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.ohs.monolithic.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@RequiredArgsConstructor
@Service
public class BoardManageService {
    final BoardRepository bRepo;

    public BoardResponse createBoard(String title, String desc){

        Board newBoard = Board.builder()
                        .title(title)
                        .createDate(LocalDateTime.now())
                        .description(desc)
                        .build();

        Board resultBoard =  bRepo.save(newBoard);
        return BoardResponse.builder()
                .id(resultBoard.getId()).title(resultBoard.getTitle()).description(resultBoard.getDescription()).build();
    }

    public List<Board> getBoards() {
        return bRepo.findAll();
    }
    public Board getBoard(Integer id){
        return bRepo.findById(id).get();
    }


    public void save(Board target) {
        bRepo.save(target);
    }
}
