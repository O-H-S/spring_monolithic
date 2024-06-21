package com.ohs.monolithic.problem.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor //
@Table(name = "problem",
        uniqueConstraints = {
          @UniqueConstraint(
                  name="uniqueInPlatform",
                  columnNames = {"platform", "platform_id"}
          )
        },
        // id가 기본 키이고 클러스터드 인덱스로 사용되고 있다면, 단독으로 id만을 위한 인덱스는 필요하지 않습니다. 그러나 복합 인덱스를 설계할 때는 id를 포함하는 것이 다양한 쿼리 시나리오에서 성능 이점을 제공
        indexes = {
                @Index(name = "idx_problem_found_date_id", columnList = "found_date DESC, id"),
                @Index(name = "idx_problem_upsert", columnList = "platform, platform_id, collector_version"),
        }
)
public class Problem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @Column(length = 64, nullable = false)
  private String platform;

  @Setter
  @Column(name = "platform_id", length = 255, nullable = false)
  private String platformId;

  @Setter
  @Column(length = 255, nullable = false)
  private String title;

  @Setter
  @Column(length = 64, nullable = true)
  private String difficulty;

  @Setter
  @Column(length = 255, nullable = false)
  private String link;

  @Setter
  @Column(name = "found_date", nullable = false)
  private LocalDateTime foundDate;

  @Setter
  @Column(name = "post_count", nullable = false)
  private Integer postCount;

  @Setter
  @Column(name = "collector_version", nullable = false)
  private Integer collectorVersion;



  @Builder
  public Problem(String platform, String platformId, String title, String difficulty, String link, LocalDateTime foundDate, Integer postCount,Integer version){

    if (foundDate == null)
      foundDate = LocalDateTime.now();
    if(postCount == null)
      postCount = 0;

    this.platform = platform;
    this.platformId = platformId;
    this.title = title;
    this.difficulty = difficulty;
    this.link = link;
    this.foundDate = foundDate;
    this.collectorVersion = version;
    this.postCount = 0;

  }




}
