package com.ohs.monolithic.board.domain;


import com.ohs.monolithic.user.domain.Account;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Entity
@Getter
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

    @Builder/*(builderClassName = "ByPrimitive", builderMethodName = "ByPrimitive")*/
    public PostLike(Post post, Account member, Boolean valid){
        Assert.notNull(post, " post must not be null");
        Assert.notNull(member, " member must not be null");
        Assert.notNull(valid, "valid must not be null");

        this.post = post;
        this.user = member;
        this.valid = valid;

        this.createDate = LocalDateTime.now();
        this.updateDate = this.createDate;// LocalDateTime 불변 객체이므로 문제 없다.
    }

}
