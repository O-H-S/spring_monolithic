package com.ohs.monolithic.board.domain;


import com.ohs.monolithic.user.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor //
@NoArgsConstructor //
@Table(name = "postlike", indexes = {
        @Index(name = "idx_postlike_post_id_member_id", columnList = "post_id, member_id"), //
})
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Account user;

    @Setter
    private Boolean valid; // 한번 더 누르면, 삭제하는 대신 값을 변경하여 비활성화 한다.
    private LocalDateTime createDate;
    @Setter
    private LocalDateTime updateDate; // 최초 좋아요를 누르거나, 다시 한번 눌러 비활성화시 변경됨.

}
