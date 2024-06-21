package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.PostTag;
import com.ohs.monolithic.common.utils.DefaultBulkInsertableRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class PostTagRepositoryImpl extends DefaultBulkInsertableRepository<PostTag, Long> {
  public PostTagRepositoryImpl(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }


  @Override
  protected String initQuery() {

    String sql = "INSERT INTO posttag (post_id, tag_id, type) VALUES (?,?,?) " +
            "ON DUPLICATE KEY UPDATE type = VALUES(type)";
    return sql;
  }

  @Override
  protected void initStatement(PostTag postTag, PreparedStatement ps, int queryId) throws SQLException {
    ps.setLong(1, postTag.getPost().getId());
    ps.setLong(2, postTag.getTag().getId());
    if (postTag.getType() != null) {
      ps.setString(3, postTag.getType().toString());
    } else {
      ps.setNull(3, java.sql.Types.VARCHAR);
    }
  }

}
