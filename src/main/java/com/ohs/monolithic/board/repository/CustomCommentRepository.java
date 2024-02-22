package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.CommentPaginationDto;
import com.ohs.monolithic.account.domain.Account;

import java.util.List;


public interface CustomCommentRepository {

  List<CommentPaginationDto> getCommentsByPost(Post post, Account viewer);

}
