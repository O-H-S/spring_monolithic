package com.ohs.monolithic.board.domain;


import com.ohs.monolithic.board.domain.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;



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
  @GeneratedValue(strategy = GenerationType.IDENTITY) // GenerationType.SEQUENCE 와 차이점 공부하기.
  private Integer id;


  @Setter
  @Column(length = 250)
  private String title;

  @Setter
  @Column(columnDefinition = "TEXT")
  private String description;

  @Setter
  @Column(nullable = true)
  private Long postCount;

  @Setter
  private LocalDateTime createDate;

  @Setter
  @Column(nullable = false)
  private Boolean deleted;

  @Builder
  public Board(String title, String description, LocalDateTime createDate){

    if(createDate == null) createDate = LocalDateTime.now();

    this.title = title;
    this.description = description;
    this.postCount = 0L;
    this.createDate = createDate;
    this.deleted = Boolean.FALSE;

  }

  // 공부하고 도입해보기.
    /*@CreationTimestamp
    @Column(nullable = false, length = 20, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(length = 20)
    private LocalDateTime updatedAt;*/



}