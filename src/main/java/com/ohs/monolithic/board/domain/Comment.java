package com.ohs.monolithic.board.domain;


import com.ohs.monolithic.account.domain.Account;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "comment", indexes = {
        //@Index(name = "idx_board_id", columnList = "board_id", unique = true), // for counting posts of board(redundant)
        @Index(name = "idx_comment_deleted_post_id_create_date", columnList = "deleted , post_id, create_date DESC"), // for quering posts

})
public class Comment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계: 하나의 Post에 여러 댓글
    @JoinColumn(name = "post_id")
    private Post post;

    @Setter
    @Column(length = 200, columnDefinition = "TEXT")
    private String content;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Account author;

    @Setter
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Setter
    private LocalDateTime modifyDate;

    @Setter
    private Long likeCount;

    @Setter
    @Column(nullable = false)
    private Boolean deleted;

    @Builder/*(builderClassName = "ByPrimitive", builderMethodName = "ByPrimitive")*/
    public Comment(Post post, String content, Account author, LocalDateTime createDate){
        if(createDate == null) createDate = LocalDateTime.now();

        this.post = post;
        this.content = content;
        this.author = author;
        this.likeCount = 0L;
        this.createDate = createDate;
        this.modifyDate = this.createDate;
        this.deleted = Boolean.FALSE;
    }


}