package com.ohs.monolithic.board.domain;


import com.ohs.monolithic.account.domain.Account;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;


@Entity
@Getter
//@AllArgsConstructor //
@NoArgsConstructor //
@Table(name = "post", indexes = {
        //@Index(name = "idx_board_id", columnList = "board_id", unique = true), // for counting posts of board(redundant)
        @Index(name = "idx_post_deleted_board_id_create_date", columnList = "deleted, board_id, create_date DESC", unique = false), // for quering posts
        @Index(name = "idx_post_deleted_author_id_create_date", columnList = "deleted, author_id, create_date DESC", unique = false ) // for quering posts

})
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Account author;

    @Setter
    private Boolean deleted;


    @Setter
    @Column(length = 200)
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String content;

    @Setter
    private Integer commentCount;

    private Long likeCount;
    private Long viewCount;





    // 엔터티가 영속성 컨텍스트에 로드되는 시점에서만 작동함.(주의)
    /*@PostLoad
    public void setDefaultValues() {
        if (this.commentCount == null) {

        }
    }*/




    @Setter
    @Column(name = "create_date")
    private LocalDateTime createDate;
    @Setter
    private LocalDateTime modifyDate;



    public void changeLikeCount(int delta){
        if(this.likeCount == null)
            this.likeCount = 0L;
        this.likeCount += delta;
    }

    public void incrementViewCount(){
        if(this.viewCount == null)
            this.viewCount = 0L;
        this.viewCount += 1;
    }
    @Builder
    public Post(Board board, Account author, String title, String content, LocalDateTime createDate){

        Assert.notNull(board, "Post Creation : board must not be null");
        Assert.notNull(author, "Post Creation : author must not be null");
        Assert.notNull(title, "Post Creation : title must not be null");
        Assert.notNull(content, "Post Creation : content must not be null");

        if (createDate == null)
            createDate = LocalDateTime.now();

        this.board = board;
        this.author = author;
        this.title = title;
        this.content = content;

        this.createDate = createDate;
        this.modifyDate = this.createDate;

        this.commentCount = 0;
        this.viewCount = 0L;
        this.likeCount = 0L;
        this.deleted = Boolean.FALSE;
    }


}