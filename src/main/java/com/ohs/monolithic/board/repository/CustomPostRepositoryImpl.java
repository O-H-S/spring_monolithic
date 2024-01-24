package com.ohs.monolithic.board.repository;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.ohs.monolithic.board.domain.*;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.user.Account;
import com.ohs.monolithic.utils.JdbcOperationsRepository;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.print.DocFlavor;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.LongFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.ohs.monolithic.board.domain.QPost.post;
import static com.ohs.monolithic.board.domain.QPostLike.postLike;
import static com.ohs.monolithic.board.domain.QPostView.postView;
import static com.ohs.monolithic.user.QAccount.account;


@Repository
//@RequiredArgsConstructor
public class CustomPostRepositoryImpl extends QuerydslRepositorySupport implements CustomPostRepository {

    private final JdbcTemplate jdbcTemplate;
    private final EntityManager em;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JPAQueryFactory queryFactory;
    public CustomPostRepositoryImpl(JdbcTemplate template, NamedParameterJdbcTemplate namedTemplate, JPAQueryFactory qF,
                                    EntityManager entityManager) {
        super(Post.class);
        jdbcTemplate = template;
        namedParameterJdbcTemplate = namedTemplate;
        queryFactory = qF;
        this.em = entityManager;
    }

    //@Override
    public Page<Post> selectAllByBoard(Pageable pageable, Board board, Long allCounts) {

        /*OrderSpecifier<?>[] orders = pageable.getSort().stream()
                .map(order -> new OrderSpecifier<>(
                        order.isAscending() ? Order.ASC : Order.DESC,
                        Expressions.path(Post.class, qPost, order.getProperty())))
                .toArray(OrderSpecifier[]::new);*/
        List<Post> posts = queryFactory
                .selectFrom(post)
                /*select(Projections.fields(BookPaginationDto.class,
                        book.id.as("bookId"),
                        book.name,
                        book.bookNo))
                .from(book)*/
                .where(
                        post.board.id.eq(board.getId())
                        // board_id 와 board.getId()가 같을때
                        //book.name.like(name + "%") // like는 뒤에 %가 있을때만 인덱스가 적용됩니다.
                )
                .orderBy(post.createDate.desc()) // 최신순으로
                //.orderBy(qPost.id.desc())
                .limit(pageable.getPageSize()) // 지정된 사이즈만큼
                .offset(pageable.getOffset()) // 지정된 페이지 위치에서
                .fetch(); // 조회

        return new PageImpl<>(posts, pageable, allCounts);
    }


    @Override
    public Page<PostPaginationDto> selectAllByBoardWithCovering(Pageable pageable, Board board, Long allCounts) {



        // 빠르게 인덱스만으로 대상 id 값들을 구해온다.
        List<Integer> ids = queryFactory
                .select(post.id)
                .from(post)
                .where(post.deleted.isFalse(), post.board.id.eq(board.getId()))
                .orderBy(post.createDate.desc())
                .limit(pageable.getPageSize()) // 지정된 사이즈만큼
                .offset(pageable.getOffset()) // 지정된 페이지 위치에서
                .fetch();

        if (CollectionUtils.isEmpty(ids)) {
            //반환 코드 개선 필요.
            return Page.empty(pageable);
        }

        QBean<PostPaginationDto> projection = createPostPaginationProjection();
        List<PostPaginationDto> results = queryFactory
                .select(projection)
                .from(post).leftJoin(post.author, account) // left outer join = left join 같은 의미로 쓰인다. (left inner join은 없다)
                .where(post.id.in(ids))
                .orderBy(post.createDate.desc())
                .fetch();

        return new PageImpl<>(results, pageable, allCounts);
        //return null;
    }

    @Override
    public List<PostPaginationDto> selectNextByBoard(Integer baseID, Board board, Integer size) {
        List<PostPaginationDto> posts = queryFactory
                .select(createPostPaginationProjection())
                .from(post).leftJoin(post.author, account)
                .where(
                        post.deleted.isFalse(), post.board.id.eq(board.getId()), ltPostId(baseID)
                )
                .orderBy(post.createDate.desc()) // 최신순으로
                //.orderBy(qPost.id.desc())
                .limit(size) // 지정된 사이즈만큼
                .fetch(); // 조회
        return posts;
    }

    @Override
    public PostLike findPostLike(Integer postID, Long memberID) {

        PostLike like = queryFactory.selectFrom(postLike)
                .where(
                        postLike.post.id.eq(postID),
                        postLike.user.id.eq(memberID)
                )
                .fetchOne();
        return like;
    }

    @Transactional
    @Override
    public PostLike savePostLike(PostLike postLike) {

        Assert.notNull(postLike, "Entity must not be null");
        if (postLike.getId() == null) {
            em.persist(postLike);
            return postLike;
        } else {
            return em.merge(postLike);
        }
        /*if (entityInformation.isNew(entity)) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }*/
    }

    // Projections.fields를 사용하는 부분을 별도의 메소드로 분리
    private QBean<PostPaginationDto> createPostPaginationProjection() {
        // as의 용도 : 가독성, 결과 재사용(참조), DTO와 매핑,
        return Projections.fields(PostPaginationDto.class,
                post.id.as("id"),
                post.title,
                account.id.as("userId"),
                account.username.as("userName"),
                post.createDate,
                post.commentCount, post.likeCount, post.viewCount);
    }

    private BooleanExpression ltPostId(Integer postID) {
        if (postID == null) {
            return null; // BooleanExpression 자리에 null이 반환되면 조건문에서 자동으로 제거된다
        }

        return post.id.lt(postID);
    }

    @Override
    public JdbcTemplate getTemplate() {
        return this.jdbcTemplate;
    }

    @Override
    public void bulkInsert(List<Post> posts){
        int count = posts.size();
        //bulkInsert( posts, null);
        _bulkInsert(posts.iterator(), (long)count,null);
    }


    public void bulkInsert(Long counts, LongFunction<Post> entityGenerator, BatchProcessor logicPerBatch) {
        if (entityGenerator == null) {
            throw new IllegalArgumentException("entityGenerator must not be null");
        }

        Stream<Post> postStream = Stream.iterate(0L, n -> n + 1) // Long 타입 인덱스 생성
                .map(entityGenerator::apply)    // 각 인덱스에 대해 entityGenerator 적용
                .limit(counts);          // counts 만큼만 제한

        _bulkInsert(postStream.iterator(), counts, logicPerBatch);
    }

    void _bulkInsert(Iterator<Post> postIterator, Long count, BatchProcessor logicPerBatch){
        final int batchSize = 4000;
        // 'id' 필드는 생략.
        // 'board', 'author', 'voter'는 외래 키 관계를 적절히 처리해야함.
        String sql = "INSERT INTO post(title, content, comment_Count, create_date, modify_Date, board_id) " +
                "VALUES (?,?,?,?,?,?)";
        int endBatch = (int)((count + batchSize - 1) / batchSize);
        for(int i = 0; i < endBatch; i++) {
            long start = (long)i * batchSize;
            long end = Math.min(count, ((long)i + 1) * batchSize);

            long startTime = System.currentTimeMillis();
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int j) throws SQLException {
                    Post post = postIterator.next();
                    ps.setString(1, post.getTitle());
                    ps.setString(2, post.getContent());
                    ps.setInt(3, 0); // 예시로 0 설정
                    ps.setTimestamp(4, Timestamp.valueOf(post.getCreateDate()));
                    ps.setDate(5, null);
                    if (post.getBoard() == null) {
                        ps.setNull(6, Types.INTEGER); // board_id에 null 설정
                    } else {
                        ps.setInt(6, post.getBoard().getId()); // board_id에 값 설정
                    }
                }
                @Override
                public int getBatchSize() {
                    return (int)(end-start);
                }
            });
            if (logicPerBatch != null)
                logicPerBatch.process(i+1, endBatch, batchSize ,i == endBatch - 1, System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public void bulkInsert(List<Post> posts, BatchProcessor logicPerBatch) {

        // 'id' 필드는 생략.
        // 'board', 'author', 'voter'는 외래 키 관계를 적절히 처리해야함.
        String sql = "INSERT INTO post(title, content, comment_Count, create_date, modify_Date, board_id) " +
                "VALUES (?,?,?,?,?,?)";

        int batchSize = 4000;
        /*IntStream.range(0, (posts.size() + batchSize - 1) / batchSize)
                .forEach(i -> {*/
        int endBatch = (posts.size() + batchSize - 1) / batchSize;
        for(int i = 0; i < endBatch; i++) {
            int start = i * batchSize;
            int end = Math.min(posts.size(), (i + 1) * batchSize);
            List<Post> batchList = posts.subList(start, end);

            long startTime = System.currentTimeMillis();
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int j) throws SQLException {
                    Post post = batchList.get(j);
                    ps.setString(1, post.getTitle());
                    ps.setString(2, post.getContent());
                    ps.setInt(3, 0); // 예시로 0 설정
                    ps.setTimestamp(4, Timestamp.valueOf(post.getCreateDate()));
                    ps.setDate(5, null);
                    if (post.getBoard() == null) {
                        ps.setNull(6, Types.INTEGER); // board_id에 null 설정
                    } else {
                        ps.setInt(6, post.getBoard().getId()); // board_id에 값 설정
                    }
                }
                @Override
                public int getBatchSize() {
                    return batchList.size();
                }
            });

            if(logicPerBatch != null)
                logicPerBatch.process(i+1, endBatch, batchSize ,i == endBatch - 1, System.currentTimeMillis() - startTime);
        }
                /*});*/

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