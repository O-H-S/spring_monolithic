package com.ohs.monolithic.board.domain;


import com.ohs.monolithic.board.domain.constants.BoardPaginationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


//@Table
// 명시적인 구성이 필요한 경우나 데이터베이스 설계에 특정 요구 사항이 있는 경우 @Table 어노테이션을 사용하여 이러한 설정을 정확하게 제어할 수 있습니다.
// @Table 어노테이션은 선택적이며, 제공하지 않는 경우 JPA 구현체는 기본 규칙에 따라 테이블 이름 및 설정을 결정
// @Table(name = "my_table"), @Table(catalog = "my_catalog", schema = "my_schema"), @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"column1", "column2"}))
// 인덱스 추가(JPA 2.1이상) @Table(indexes = @Index(columnList = "column1, column2", name = "my_index")
@Entity
@Getter
@NoArgsConstructor // JPA에서 필요하다.
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;


  @Setter
  @Column(length = 250)
  private String title;

  @Setter
  @Column(columnDefinition = "TEXT")
  private String description;

  @Setter
  @Column
  private Long postCount;

  @Setter
  private LocalDateTime createDate;


  // 차후, AttributeConverter를 통해 사용되지 않는 타입을 처리할 수 있다.
  @Setter
  @Enumerated(EnumType.STRING)
  private BoardPaginationType paginationType;

  // Hybrid Pagination 타입일 때, 몇개의 포스트부터 Cursor 방식으로 전환 되는지 결정.
  @Setter
  private Long thresholdForCursor;

  @Setter
  @Column(nullable = false)
  private Boolean deleted;

  @Builder
  public Board(String title, String description, LocalDateTime createDate, BoardPaginationType paginationType, Long thresholdForCursor){

    if(createDate == null) createDate = LocalDateTime.now();
    if(paginationType == null) paginationType = BoardPaginationType.Offset_CountCache_CoveringIndex;
    if(paginationType.equals(BoardPaginationType.Hybrid) && thresholdForCursor == null) thresholdForCursor = 10000L;

    this.title = title;
    this.description = description;
    this.postCount = 0L;
    this.createDate = createDate;
    this.deleted = Boolean.FALSE;
    this.paginationType = paginationType;
    this.thresholdForCursor = thresholdForCursor;

  }

  // 공부하고 도입해보기.
    /*@CreationTimestamp
    @Column(nullable = false, length = 20, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(length = 20)
    private LocalDateTime updatedAt;*/



}