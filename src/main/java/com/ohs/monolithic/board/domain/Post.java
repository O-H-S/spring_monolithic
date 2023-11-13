package com.ohs.monolithic.board.domain;


import com.ohs.monolithic.user.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Builder
@AllArgsConstructor // @Builder 패턴에서 내부적으로 필요하다.
@NoArgsConstructor // @Builder 패턴에서 내부적으로 필요하다.
@Table(name = "post", indexes = {
        //@Index(name = "idx_board_id", columnList = "board_id", unique = true), // for counting posts of board(redundant)
        @Index(name = "idx_board_id_create_date", columnList = "board_id, create_date DESC", unique = true), // for quering posts
        @Index(name = "idx_id_create_date", columnList = "create_date DESC", unique = true) // for quering posts
})
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // mappedBy : 주인임을 설정한다. (Comment.post 필드와 매핑)
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> commentList;


    private Integer commentCount;


    // 엔터티가 영속성 컨텍스트에 로드되는 시점에서만 작동함.(주의)
    /*@PostLoad
    public void setDefaultValues() {
        if (this.commentCount == null) {

        }
    }*/


    @ManyToOne(fetch = FetchType.LAZY)
    private Account author;

    @ManyToMany
    private Set<Account> voter;

    @Column(name = "create_date")
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

    public void setCommentCount(int size) {
        commentCount = size;
    }
}