package com.ohs.monolithic.board.dto;


import com.ohs.monolithic.board.domain.constants.BoardPaginationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(force = true)
public class BoardCreationForm {

    @NotNull
    @Size(min = 1, max = 100)
    private String title;

    private String desc = "";

    private BoardPaginationType paginationType;

}
