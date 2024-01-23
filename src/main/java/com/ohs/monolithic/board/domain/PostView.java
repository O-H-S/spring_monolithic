package com.ohs.monolithic.board.domain;

import com.ohs.monolithic.user.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor //
@Table(name = "postview", indexes = {
        @Index(name = "idx_postview_post_id_member_id", columnList = "post_id, member_id"), //
})
public class PostView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계: 하나의 Post에 여러 댓글
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Account user;

    private Integer count; // 몇번 조회했는지

    private LocalDateTime createDate; // 최초 조회 시점
    private LocalDateTime updateDate; // 최근 조회 시점

    public void incrementCount() {
        count += 1;
    }


    @Builder
    public PostView(Post post, Account member, LocalDateTime createDate){

        if(createDate == null)
            createDate = LocalDateTime.now();


        this.post = post;
        this.user = member;
        this.count = 1;
        this.createDate = createDate;
        this.updateDate = createDate;



    }

}
