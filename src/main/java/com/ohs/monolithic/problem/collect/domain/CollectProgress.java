package com.ohs.monolithic.problem.collect.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor //
@Table(name = "collect_progress",
        indexes = {

        }
)
public class CollectProgress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Setter
  @Column(length = 64, nullable = false)
  private String platform;

  @Setter
  @Column(name = "collector_version", nullable = false)
  private Integer collectorVersion;

  @Setter
  @Column(name = "last_window", nullable = false)
  private Integer lastWindow;

  @Setter
  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

}
