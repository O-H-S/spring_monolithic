package com.ohs.monolithic.common.utils;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.function.LongFunction;


@NoRepositoryBean
public interface BulkInsertableRepository<T, ID> {
    @FunctionalInterface
    public interface BatchProcessor {
        void process(Integer curBatch, Integer maxBatch, Integer batchSize ,Boolean finished, Long executionTime);
    }

    void bulkInsert(List<T> targets);

    void bulkInsert(List<T> targets, BatchProcessor logicPerBatch);


    void bulkInsert(Long counts, LongFunction<T> entityGenerator, BatchProcessor logicPerBatch);

}
