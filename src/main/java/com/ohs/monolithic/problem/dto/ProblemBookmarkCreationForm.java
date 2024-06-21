package com.ohs.monolithic.problem.dto;

import com.ohs.monolithic.problem.domain.ProblemBookmarkType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProblemBookmarkCreationForm {
  private ProblemBookmarkType type;
}
