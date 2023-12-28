package com.ohs.monolithic.board.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class BoardCreationForm {

    @NotNull
    @Size(min = 1, max = 100)
    final private String title;

    @Builder.Default
    final private String desc = "";

}
