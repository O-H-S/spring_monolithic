package com.ohs.monolithic.utils;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@NoRepositoryBean
public interface JdbcOperationsRepository<R, T> {
    JdbcTemplate getTemplate();
    void bulkInsert(List<T> targets);
}
