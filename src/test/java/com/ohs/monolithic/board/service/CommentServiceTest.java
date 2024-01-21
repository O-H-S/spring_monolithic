package com.ohs.monolithic.board.service;

import com.ohs.monolithic.board.repository.BoardRepository;
import com.ohs.monolithic.board.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(MockitoExtension.class)
@Tag("base")
@Tag("unit")
public class CommentServiceTest {

  @InjectMocks
  private CommentService target;


  @Mock
  private CommentRepository mockRepository;



}
