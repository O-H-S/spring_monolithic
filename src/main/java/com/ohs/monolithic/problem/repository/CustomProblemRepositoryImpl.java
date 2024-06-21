package com.ohs.monolithic.problem.repository;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.ohs.monolithic.common.utils.DefaultBulkInsertableRepository;
import com.ohs.monolithic.problem.domain.Problem;
import com.ohs.monolithic.problem.dto.ProblemPaginationDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
  public Page<ProblemPaginationDto> selectAll(Pageable pageable, Long viewerId) {

    // 빠르게 인덱스만으로 대상 id 값들을 구해온다.
    List<Long> ids = queryFactory
            .select(problem.id)
            .from(problem)
            .orderBy(problem.foundDate.desc())
            .limit(pageable.getPageSize()) // 지정된 사이즈만큼
            .offset(pageable.getOffset()) // 지정된 페이지 위치에서
            .fetch();

    if (CollectionUtils.isEmpty(ids)) {
      return Page.empty(pageable);
    }

    QBean<ProblemPaginationDto> projection = createPaginationProjection(viewerId);

    // viewerId 가 null일 때, eq(viewerId)는 에러 발생
    BooleanExpression joinCondition = viewerId != null
            ? problemBookmark.account.id.eq(viewerId).and(problem.id.eq(problemBookmark.problem.id))
            : Expressions.FALSE;

    List<ProblemPaginationDto> results = queryFactory
            .select(projection)
            .from(problem)
            .leftJoin(problemBookmark)
            .on(joinCondition)
            .where(problem.id.in(ids))
            .orderBy(problem.foundDate.desc())
            .fetch();

    Long totalCount = queryFactory
            .select(problem.count())
            .from(problem)
            .fetchOne();

    return new PageImpl<>(results, pageable, totalCount);
  }

  @Override
  public List<Problem> findProblemsWithLock(ArrayList<String> platform, ArrayList<String> Ids) {
    BooleanBuilder predicate = new BooleanBuilder();
    for (int i = 0; i < platform.size(); i++)
    {
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
            problem.link,
            problem.postCount.as("postCount"),
            problem.foundDate.as("foundDate"),
            problemBookmark.bookmarkType.as("bookmarkType")
    );
  }
  @Override
  protected String[] initQueries() {

    String insertQuery = "INSERT IGNORE INTO problem (platform, platform_id, title, difficulty, link, found_date, post_count, collector_version) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ;";

    String updateQuery = "UPDATE problem SET " +
            "title = ?, difficulty = ?, link = ?, found_date = ?, post_count = ?, collector_version = ? " +
            "WHERE platform = ? AND platform_id = ? AND collector_version <= ?";
    return new String[]{ insertQuery, updateQuery };
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
        ps.setObject(id++, problem.getLink());
        ps.setObject(id++, Timestamp.valueOf(problem.getFoundDate()));
        ps.setObject(id++, problem.getPostCount());
        ps.setObject(id++, problem.getCollectorVersion());
      }
      case 1 ->{
        ps.setObject(id++, problem.getTitle());
        ps.setObject(id++, problem.getDifficulty());
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
