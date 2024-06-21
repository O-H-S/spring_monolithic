package com.ohs.monolithic.problem.collect.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class CollectProgressUpdateForm {
  Integer targetWindow;
  //Boolean applyIfExists; // Problem 데이터가 존재할 때에만 필드를 업데이트한다.
  List<ProblemDto> problemList;

}
