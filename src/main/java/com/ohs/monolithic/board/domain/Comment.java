package com.ohs.monolithic.board.domain;


import com.ohs.monolithic.user.Account;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Getter
public class Comment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계: 하나의 Post에 여러 댓글
    private Post post;

/*    @ManyToOne(fetch = FetchType.LAZY)
    private User user;*/

    @Column(length = 200, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account author;

    @ManyToMany
    private Set<Account> voter;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public void setContent(String content) {
        this.content = content;

    }

    public void setCreateDate(LocalDateTime now) {
        createDate = now;

    }

    public void setPost(Post target) {
        post = target;

    }

    public void setAuthor(Account account) {
        this.author = account;

    }

    public void setModifyDate(LocalDateTime now) {
        this.modifyDate = now;
    }
}