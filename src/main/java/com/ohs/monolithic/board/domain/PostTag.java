package com.ohs.monolithic.board.domain;

import com.ohs.monolithic.board.domain.constants.PostTagType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

@Entity
@Getter
@NoArgsConstructor //
@Table(name = "posttag",
        // unique 제약조건을 추가하면 자동으로 관련 인덱스(같은 이름으로) 생성됨.
        uniqueConstraints = {
                @UniqueConstraint(
                        name="uniquePostTagPair",
                        columnNames = {"post_id", "tag_id"}
                )
        }
)
public class PostTag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @JoinColumn(nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Post post;

  @Setter
  @JoinColumn(nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Tag tag;

  @Setter
  @Enumerated(EnumType.STRING)
  private PostTagType type;
  // type이 null이면, 태그 지정을 취소한것. (deleted 대체)

  @Builder
  public PostTag(Post post, Tag tag, PostTagType type){
    Assert.notNull(post, "PostTag Creation : post must not be null");
    Assert.notNull(tag, "PostTag Creation : tag must not be null");

    this.post = post;
    this.tag = tag;
    this.type = type;
  }
}
