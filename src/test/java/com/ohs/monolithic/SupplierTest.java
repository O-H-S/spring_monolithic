package com.ohs.monolithic;

import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.utils.BoardTestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

public class SupplierTest {

  @Test
  void checkInstanceIdentity(){
    Supplier<Board> boardSupplier = () -> BoardTestUtils.createBoardSimple(1, "test");
    Board board = boardSupplier.get();
    Board board2 = boardSupplier.get();

    assertThat(board).isNotSameAs(board2);
  }

}
