package com.ohs.monolithic.utils;

import jakarta.annotation.PostConstruct;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.Console;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.function.LongFunction;
import java.util.stream.Stream;


// 리팩토링 : 일반화된 initQuery, initStatement를 작성하면, 어떤 Entity에서도 Override없이 사용 가능. AbstractBulkInsertableRepository도 필요없어짐.
@Repository
@RequiredArgsConstructor
//@NoArgsConstructor(access = AccessLevel.PROTECTED) //
public class DefaultBulkInsertableRepository<T, ID> extends AbstractBulkInsertableRepository<T, ID> {

  final protected JdbcTemplate jdbcTemplate;


  @Override
  public void bulkInsert(List<T> targets) {

  }

  @Override
  public void bulkInsert(List<T> targets, BatchProcessor logicPerBatch) {

  }

  @Override
  public void bulkInsert(Long counts, LongFunction<T> entityGenerator, BatchProcessor logicPerBatch) {
    Stream<T> entityStream = Stream.iterate(0L, n -> n + 1) // Long 타입 인덱스 생성
            .map(entityGenerator::apply)    // 각 인덱스에 대해 entityGenerator 적용
            .limit(counts);
    try{
      _bulkInsert(entityStream.iterator(), counts, logicPerBatch);
    }catch (Exception e){
      System.out.println(e.getMessage());
      throw e;
    }

  }

  void _bulkInsert(Iterator<T> postIterator, Long count, BatchProcessor logicPerBatch) {
    final int batchSize = 4000;

    String sql = this.initQuery();

    int endBatch = (int) ((count + batchSize - 1) / batchSize);
    for (int i = 0; i < endBatch; i++) {
      long start = (long) i * batchSize;
      long end = Math.min(count, ((long) i + 1) * batchSize);

      long startTime = System.currentTimeMillis();
      jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int j) throws SQLException {
          T targetEntity = postIterator.next();
          initStatement(targetEntity, ps);

        }

        @Override
        public int getBatchSize() {
          return (int) (end - start);
        }
      });
      if (logicPerBatch != null)
        logicPerBatch.process(i + 1, endBatch, batchSize, i == endBatch - 1, System.currentTimeMillis() - startTime);

    }
  }


  @Override // 상속하는 클래스에서 구현해야함.
  protected String initQuery() {
    throw new UnsupportedOperationException("initQuery() must be implemented");
  }

  @Override
  protected void initStatement(T target, PreparedStatement ps) throws SQLException {
    throw new UnsupportedOperationException("initStatement() must be implemented");
  }
}
