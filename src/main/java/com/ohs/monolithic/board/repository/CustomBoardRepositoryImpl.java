package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BoardResponse;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.ohs.monolithic.board.domain.QBoard.board;

@Repository
public class CustomBoardRepositoryImpl extends QuerydslRepositorySupport implements CustomBoardRepository{
    private final JPAQueryFactory queryFactory;

    public CustomBoardRepositoryImpl(JPAQueryFactory qF) {
        super(Board.class);
        queryFactory = qF;

    }

    @Override
    public List<BoardResponse> getAllBoards(boolean includeTitle, boolean includeDesc) {

        List<Expression<?>> expressions = new ArrayList<>();
        expressions.add(board.id);

        if (includeTitle) {
            expressions.add(board.title);
        }
        if (includeDesc) {
            expressions.add(board.description);
        }


        Expression<BoardResponse> projection = Projections.bean(BoardResponse.class, expressions.toArray(new Expression[0])); // or Expression[]::new
        //Expression<BoardResponse> projection = Projections.bean(BoardResponse.class,board.id, board.title,null);

        List<BoardResponse> boards = queryFactory
                .select(projection)
                .from(board)
                .fetch(); // 조회


        return boards;
    }
}
