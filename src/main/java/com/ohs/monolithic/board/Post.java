package com.ohs.monolithic.board;


import com.ohs.monolithic.user.Account;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Entity
@Getter
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Comment> commentList;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account author;

    @ManyToMany
    private Set<Account> voter;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreateDate(LocalDateTime now) {
        createDate = now;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setAuthor(Account user) {
        author = user;

    }

    public void setModifyDate(LocalDateTime now) {
        this.modifyDate = now;
    }
}