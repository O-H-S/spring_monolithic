package com.ohs.monolithic.problem.repository;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.ohs.monolithic.common.utils.DefaultBulkInsertableRepository;
import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.domain.QProblem;
import com.ohs.monolithic.problem.dto.ProblemPaginationDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.ohs.monolithic.problem.domain.QProblem.problem;
import static com.ohs.monolithic.problem.domain.QProblemBookmark.problemBookmark;

@Repository

public class CustomProblemRepositoryImpl extends DefaultBulkInsertableRepository<Problem, Long> implements CustomProblemRepository {
  final private JPAQueryFactory queryFactory;

  public CustomProblemRepositoryImpl(JdbcTemplate jdbcTemplate, JPAQueryFactory queryFactory) {
    super(jdbcTemplate);
    this.queryFactory = queryFactory;
  }


  @Override
  public Page<ProblemPaginationDto> selectByDetails(Pageable pageable, Long viewerId, String keywords, Boolean keywords_booleanMode, List<String> platforms, Float[] levelRange) {

    var coreQuery = queryFactory.from(problem);

    if (levelRange != null) {
      coreQuery.
              where(
                      problem.level.isNull().or(
                              problem.level.between(levelRange[0], levelRange[1]) // between 메서드는 기본적으로 양쪽 경계를 포함하는(inclusive) 범위
                      )
              );

    }

    // 검색어 필터 추가
    if (keywords != null && !keywords.isEmpty()) {
      //keywords = "'" + keywords + "'";
      //keywords =keywords
      if (keywords_booleanMode == Boolean.TRUE) {
        NumberExpression<Double> matchScore = Expressions.numberTemplate(Double.class,
                "function('match_against_booleanmode',{0},{1})",
                problem.title,
                keywords);

        coreQuery.where(matchScore.gt(0.0));
      } else {
        NumberExpression<Double> matchScore = Expressions.numberTemplate(Double.class,
                "function('match_against',{0},{1})",
                problem.title,
                keywords);
        coreQuery.where(matchScore.gt(0.0));
      }
      // 커버링 인덱스를 위해서는, title 인덱스 추가해야함. (단일? 기존에 추가? INDEX MERGE가 일어나는지 확인)
    }

    // 플랫폼 필터 추가
    if (platforms != null) {
      if (platforms.isEmpty())
        return Page.empty(pageable);
      BooleanExpression platformCondition = problem.platform.in(platforms);
      coreQuery.where(platformCondition);
    }

    var idQuery = coreQuery.clone();
    // 정렬 조건 추가
    for (OrderSpecifier<?> orderSpecifier : getOrderSpecifier(pageable.getSort(), problem)) {
      idQuery.orderBy(orderSpecifier);
    }

    List<Long> ids = idQuery.select(problem.id)
            .limit(pageable.getPageSize()) // 지정된 사이즈만큼
            .offset(pageable.getOffset())
            .fetch();

    if (CollectionUtils.isEmpty(ids)) {
      return Page.empty(pageable);
    }

    QBean<ProblemPaginationDto> projection = createPaginationProjection(viewerId);

    // viewerId 가 null일 때, eq(viewerId)는 에러 발생
    BooleanExpression joinCondition = viewerId != null
            ? problemBookmark.account.id.eq(viewerId).and(problem.id.eq(problemBookmark.problem.id))
            : Expressions.FALSE;

    var resultQuery = queryFactory
            .select(projection)
            .from(problem)
            .leftJoin(problemBookmark)
            .on(joinCondition)
            .where(problem.id.in(ids));

    for (OrderSpecifier<?> orderSpecifier : getOrderSpecifier(pageable.getSort(), problem)) {
      resultQuery.orderBy(orderSpecifier);
    }
    List<ProblemPaginationDto> results = resultQuery.fetch();

    Long totalCount = coreQuery
            .select(problem.count())
            .from(problem)
            .fetchFirst();

    return new PageImpl<>(results, pageable, totalCount);
  }

  @Override
  public Page<ProblemPaginationDto> selectAll(Pageable pageable, Long viewerId) {
    return selectByDetails(pageable, viewerId, null, null, null, null);
  }

  @Override
  public Page<ProblemPaginationDto> selectByKeywords(Pageable pageable, Long viewerId, String keywords) {
    return selectByDetails(pageable, viewerId, keywords, null, null, null);
  }

  @Override
  public Page<ProblemPaginationDto> selectByKeywordsWithBooleanMode(Pageable pageable, Long viewerId, String keywords) {
    return selectByDetails(pageable, viewerId, keywords, true, null, null);
  }

  @Override
  public Page<ProblemPaginationDto> selectByPlatforms(Pageable pageable, Long viewerId, List<String> platforms) {
    return selectByDetails(pageable, viewerId, null, null, platforms, null);
  }

  @Override
  public Page<ProblemPaginationDto> selectByLevelRange(Pageable pageable, Long viewerId, Float[] levelRange) {
    return selectByDetails(pageable, viewerId, null, null, null, levelRange);
  }



  private List<OrderSpecifier<?>> getOrderSpecifier(Sort sort, QProblem problem) {
    List<OrderSpecifier<?>> orders = new ArrayList<>();

    for (Sort.Order order : sort) {
      Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
      String property = order.getProperty();


      OrderSpecifier<?> orderSpecifier = getOrderSpecifierForProperty(property, direction, problem);
      if (orderSpecifier != null) {
        orderSpecifier = orderSpecifier.nullsLast();
        orders.add(orderSpecifier);
      }
    }

    return orders;
  }

  private OrderSpecifier<?> getOrderSpecifierForProperty(String property, Order direction, QProblem problem) {
    Path<?> path = Expressions.path(Object.class, problem, property);
    return new OrderSpecifier(direction, path);
  }

  @Override
  public List<Problem> findProblemsWithLock(ArrayList<String> platform, ArrayList<String> Ids) {
    BooleanBuilder predicate = new BooleanBuilder();
    for (int i = 0; i < platform.size(); i++) {
      predicate.or(problem.platform.eq(platform.get(i))
              .and(problem.platformId.eq(Ids.get(i))));
    }

    return queryFactory.selectFrom(problem)
            .where(predicate)
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetch();
  }

  private QBean<ProblemPaginationDto> createPaginationProjection(Long viewerId) {
    // as의 용도 : 가독성, 결과 재사용(참조), DTO와 매핑,

    return Projections.fields(ProblemPaginationDto.class,
            problem.id.as("id"),
            problem.platform,
            problem.title,
            problem.difficulty,
            problem.level,
            problem.link,
            problem.postCount.as("postCount"),
            problem.foundDate.as("foundDate"),
            problemBookmark.bookmarkType.as("bookmarkType")
    );
  }

  @Override
  protected String[] initQueries() {

    String insertQuery = "INSERT IGNORE INTO problem (platform, platform_id, title, difficulty, level, link, found_date, post_count, collector_version) " +
            "VALUES (?, ?, ?, ?, ?,?,  ?, ?, ?) ;";

    String updateQuery = "UPDATE problem SET " +
            "title = ?, difficulty = ?, level = ?, link = ?, found_date = ?, post_count = ?, collector_version = ? " +
            "WHERE platform = ? AND platform_id = ? AND collector_version <= ?";
    return new String[]{insertQuery, updateQuery};
  }

  @Override
  protected void initStatement(Problem problem, PreparedStatement ps, int queryId) throws SQLException {

    int id = 1;
    switch (queryId) {
      case 0 -> {
        ps.setObject(id++, problem.getPlatform());
        ps.setObject(id++, problem.getPlatformId());
        ps.setObject(id++, problem.getTitle());
        ps.setObject(id++, problem.getDifficulty());
        ps.setObject(id++, problem.getLevel());
        ps.setObject(id++, problem.getLink());
        ps.setObject(id++, Timestamp.valueOf(problem.getFoundDate()));
        ps.setObject(id++, problem.getPostCount());
        ps.setObject(id++, problem.getCollectorVersion());
      }
      case 1 -> {
        ps.setObject(id++, problem.getTitle());
        ps.setObject(id++, problem.getDifficulty());
        ps.setObject(id++, problem.getLevel());
        ps.setObject(id++, problem.getLink());
        ps.setObject(id++, Timestamp.valueOf(problem.getFoundDate()));
        ps.setObject(id++, problem.getPostCount());
        ps.setObject(id++, problem.getCollectorVersion());
        ps.setObject(id++, problem.getPlatform());
        ps.setObject(id++, problem.getPlatformId());
        ps.setObject(id++, problem.getCollectorVersion());

      }
    }


  }


}
