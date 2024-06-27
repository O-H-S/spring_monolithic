package com.ohs.monolithic.board.domain;


import com.ohs.monolithic.account.domain.Account;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "boardpermission", indexes = {
        @Index(name = "idx_boardpermission", columnList = "board_id, name"),

})
@NoArgsConstructor
public class BoardPermission {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id")
  private Board board;

  @Setter
  @Column(length = 255, nullable = false)
  private String name;

  @Setter
  @Column(columnDefinition = "TEXT")
  private String value;

  @Builder
  public BoardPermission(Board board, String name, String value){
    //

      this.board =  board;
      this.name = name;
      this.value = value;
  }

}
