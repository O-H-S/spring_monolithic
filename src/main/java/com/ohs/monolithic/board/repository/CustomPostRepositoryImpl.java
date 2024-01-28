package com.ohs.monolithic.board.repository;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.ohs.monolithic.board.domain.*;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.utils.DefaultBulkInsertableRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

import static com.ohs.monolithic.board.domain.QPost.post;
import static com.ohs.monolithic.user.QAccount.account;



@Repository
public class CustomPostRepositoryImpl extends DefaultBulkInsertableRepository<Post, Long> implements CustomPostRepository {


  final private JPAQueryFactory queryFactory;


  public CustomPostRepositoryImpl(JdbcTemplate jdbcTemplate, JPAQueryFactory queryFactory, JdbcTemplate jdbcTemplate1) {
    super(jdbcTemplate);
    this.queryFactory = queryFactory;
  }

  //final private JdbcTemplate jdbcTemplate;


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
    List<Long> ids = queryFactory
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
  public List<PostPaginationDto> selectNextByBoard(Long baseID, Board board, Integer size) {
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

  private BooleanExpression ltPostId(Long postID) {
    if (postID == null) {
      return null; // BooleanExpression 자리에 null이 반환되면 조건문에서 자동으로 제거된다
    }

    return post.id.lt(postID);
  }


  // 리팩토링 : Post 클래스의 필드 구성이 변경되면, 아래 코드들도 변경 되도록 자동화
  @Override
  protected String initQuery() {
    // 'id' 필드는 생략.
    // 'board', 'author'는 외래 키 관계를 적절히 처리해야함.

    String sql = "INSERT INTO post(title, content, comment_count, create_date, modify_date, board_id, deleted, like_count, view_count, author_id) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?)";
    return sql;
  }

  @Override
  protected void initStatement(Post post, PreparedStatement ps) throws SQLException {

    ps.setString(1, post.getTitle());
    ps.setString(2, post.getContent());
    ps.setInt(3, 0); // 예시로 0 설정
    ps.setTimestamp(4, Timestamp.valueOf(post.getCreateDate()));
    ps.setTimestamp(5, Timestamp.valueOf(post.getModifyDate()));
    if (post.getBoard() == null) {
      ps.setNull(6, Types.INTEGER); // board_id에 null 설정
    } else {
      ps.setInt(6, post.getBoard().getId()); // board_id에 값 설정
    }
    ps.setInt(7, post.getDeleted() ? 1 : 0);
    ps.setLong(8, post.getLikeCount());
    ps.setLong(9, post.getViewCount());
    ps.setLong(10, post.getAuthor().getId()); // board_id에 값 설정
  }


}