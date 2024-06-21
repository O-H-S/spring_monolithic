package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.*;
import com.ohs.monolithic.board.dto.CommentPaginationDto;
import com.ohs.monolithic.account.domain.Account;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

import static com.ohs.monolithic.board.domain.QComment.comment;
import static com.ohs.monolithic.account.domain.QAccount.account;
import static com.ohs.monolithic.board.domain.QCommentLike.commentLike;

@Repository

public class CustomCommentRepositoryImpl extends QuerydslRepositorySupport implements CustomCommentRepository {
  private final JPAQueryFactory queryFactory;
  public CustomCommentRepositoryImpl(JPAQueryFactory qF) {
    super(Comment.class);
    queryFactory = qF;

  }

  @Override
  public List<CommentPaginationDto> getCommentsByPost(Post post, Account viewer) {

    List<Long> ids = queryFactory
            .select(comment.id)
                    .from(comment)
                            .where(comment.deleted.isFalse(), comment.post.id.eq(post.getId()))
                                    .orderBy(comment.createDate.desc())
                                            .fetch();

    return fetchDtoList(ids, viewer!=null? viewer.getId() : null);
  }

  @Override
  public CommentPaginationDto getCommentById(Long commentId, Long accountId) {
    List<CommentPaginationDto> result = fetchDtoList(Collections.singletonList(commentId), accountId);
    if(result.isEmpty()){
      throw new EntityNotFoundException("no comment found");
    }
    return result.get(0);
  }

  List<CommentPaginationDto> fetchDtoList(List<Long> ids, Long viewerId){
    if(ids.isEmpty()){
      return Collections.emptyList();
    }

    //Projections.constructor 는 인자들의 순서가 중요함. (DTO 생성자 변경시 수정필요)
    //Projections.fields는 순서 상관 없음. 이름으로 판별

    BooleanExpression likedExpression = viewerId != null
            ? commentLike.member.id.eq(viewerId).and(commentLike.valid.eq(true))
            : Expressions.asBoolean(false);

    List<CommentPaginationDto> results;
    JPAQuery<CommentPaginationDto> query = queryFactory.
            select(
                    Projections.fields(CommentPaginationDto.class,
                            comment.id,
                            comment.content,
                            comment.post.id.as("postId"),
                            account.id.as("writerId"),
                            account.nickname.as("writerNickname"),
                            account.profileImage.as("writerProfile"),
                            comment.likeCount,
                            likedExpression.as("liked"),
                            comment.createDate,
                            comment.modifyDate
                    )
            )
            .from(comment)
            .leftJoin(comment.author, account);
    if(viewerId != null) {
      query = query.leftJoin(commentLike).on(comment.id.eq(commentLike.comment.id)
              .and(commentLike.member.id.eq(viewerId)));
    }

    results = query.where(comment.id.in(ids), comment.deleted.isFalse())
            .orderBy(comment.createDate.desc())
            .fetch();


    // join의 on을 충족하지 못할 경우, null이 된다.
    results.forEach(dto -> {
      if(dto.getLiked() == null)
        dto.setLiked(false);
    });

    return results;
  }

}
