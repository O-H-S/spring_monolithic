package com.ohs.monolithic.board.service;

import com.ohs.monolithic.board.repository.BoardRepository;
import com.ohs.monolithic.board.repository.CommentLikeRepository;
import com.ohs.monolithic.board.repository.CommentRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@Tag("base")
@Tag("unit")
class CommentLikeServiceTest {

  @InjectMocks
  private CommentLikeService target;

  @Mock
  private CommentLikeRepository mockCommentLikeRepository;
  @Mock
  private CommentRepository mockCommentRepository;




}