package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Tag;
import com.ohs.monolithic.common.utils.DefaultBulkInsertableRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;


@Repository
public class TagRepositoryImpl extends DefaultBulkInsertableRepository<Tag, Long> {
  public TagRepositoryImpl(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }

  @Override
  protected String initQuery() {

    String sql = "INSERT INTO tag (name) VALUES (?) " +
            "ON DUPLICATE KEY UPDATE name = VALUES(name)";
    return sql;
  }

  @Override
  protected void initStatement(Tag tag, PreparedStatement ps, int queryId) throws SQLException {
    ps.setString(1, tag.getName());
  }

}
