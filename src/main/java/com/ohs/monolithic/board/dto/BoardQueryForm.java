package com.ohs.monolithic.board.dto;


import lombok.*;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
public class BoardQueryForm {

    private Boolean includesTitle = Boolean.TRUE;
    private Boolean includesDesc = Boolean.TRUE;
    private Boolean includesPostCounts = Boolean.TRUE;

}
