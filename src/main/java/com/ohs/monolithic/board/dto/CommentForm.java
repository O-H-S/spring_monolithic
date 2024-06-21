package com.ohs.monolithic.board.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentForm {
    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;
}