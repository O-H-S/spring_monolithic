package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.utils.JdbcOperationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;


@RequiredArgsConstructor
public class PostRepositoryImpl implements JdbcOperationsRepository<PostRepository, Post> {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Override
    public JdbcTemplate getTemplate() {
        return this.jdbcTemplate;
    }

    @Override
    public void bulkInsert(List<Post> posts) {

        // 'id' 필드는 생략.
        // 'board', 'author', 'voter'는 외래 키 관계를 적절히 처리해야함.
       /* String sql = "INSERT INTO post(title, content, comment_Count, create_Date, board_id, author_id, modify_Date) " +
                "VALUES (:title, :content, :commentCount, :createDate, :board_id, :author_id ,:modifyDate)";*/
        String sql = "INSERT INTO post(title, content, comment_Count, create_Date, modify_Date) " +
                "VALUES (?,?,?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                Post post = posts.get(i);
                preparedStatement.setString(1, post.getTitle());
                preparedStatement.setString(2, post.getContent());
                preparedStatement.setInt(3, /*post.getCommentCount()*/ 0);
                preparedStatement.setDate(4, null);
                preparedStatement.setDate(5, null);

            }

            @Override
            public int getBatchSize() {
                return posts.size();
            }
        });
    }

    /*public void bulkInsert(List<Post> posts) {

        // 'id' 필드는 생략.
        // 'board', 'author', 'voter'는 외래 키 관계를 적절히 처리해야함.
       *//* String sql = "INSERT INTO post(title, content, comment_Count, create_Date, board_id, author_id, modify_Date) " +
                "VALUES (:title, :content, :commentCount, :createDate, :board_id, :author_id ,:modifyDate)";*//*
        String sql = "INSERT INTO post(title, content, comment_Count, create_Date, modify_Date) " +
                "VALUES (:title, :content, :commentCount, :createDate, modifyDate)";

        // Convert the list of posts into an array of SqlParameterSource
        SqlParameterSource[] params = posts.stream()
                .map(post -> {
                    BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(post);
                    //paramSource.registerSqlType("board_id", Types.INTEGER)
                    *//*paramSource.registerSqlType("board", Types.INTEGER);
                    paramSource.registerSqlType("author", Types.INTEGER);*//*
                    //  paramSource.registerSqlType("board", Types.INTEGER);
                    return paramSource;
                })
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }*/
}