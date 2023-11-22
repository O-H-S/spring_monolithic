package com.ohs.monolithic.utils;

import com.ohs.monolithic.board.domain.Post;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.function.LongFunction;

@NoRepositoryBean
public interface JdbcOperationsRepository<R, T> {
    JdbcTemplate getTemplate();
    @FunctionalInterface
    public interface BatchProcessor {
        void process(Integer curBatch, Integer maxBatch, Integer batchSize ,Boolean finished, Long executionTime);
    }

    void bulkInsert(List<T> targets);

    void bulkInsert(List<T> targets, BatchProcessor logicPerBatch);
    public void bulkInsert(Long counts, LongFunction<Post> entityGenerator, BatchProcessor logicPerBatch);
}
