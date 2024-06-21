package com.ohs.monolithic.board.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor //
@Table(name = "tag", indexes = {
        //@Index(name = "idx_tag_name", columnList = "name", unique = true),

})
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(length = 255, unique = true)
  private String name;


  @Builder
  public Tag(String name){
    Assert.notNull(name, "Tag Creation : tag name must not be null");
    Assert.hasLength(name, "Tag Creation : tag name must not be empty");

    this.name = name;
  }

}
