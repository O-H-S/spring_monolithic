package com.ohs.monolithic.problem.domain;

import com.ohs.monolithic.board.event.PostCreateEvent;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProblemPostCreateEvent extends PostCreateEvent {
  Problem problem;
  public ProblemPostCreateEvent(Problem problem) {
    super(null);
    this.problem = problem;
  }
}
