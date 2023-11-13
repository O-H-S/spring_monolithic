package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.utils.JdbcOperationsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomPostRepository extends JdbcOperationsRepository<PostRepository, Post> {
    Page<Post> selectAllByBoard(Pageable pageable, Board board, Long allCounts);
    Page<PostPaginationDto> selectAllByBoardWithCovering(Pageable pageable, Board board, Long allCounts);
    List<PostPaginationDto> selectNextByBoard(Integer baseID, Board board, Integer size);
}
