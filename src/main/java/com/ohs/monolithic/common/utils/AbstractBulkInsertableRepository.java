package com.ohs.monolithic.common.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractBulkInsertableRepository<T, ID> implements BulkInsertableRepository<T, ID> {

  protected abstract String initQuery();
  protected abstract String[] initQueries();
  protected abstract void initStatement(T target, PreparedStatement ps, int queryId) throws SQLException;


}