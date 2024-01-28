package com.ohs.monolithic.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractBulkInsertableRepository<T, ID> implements BulkInsertableRepository<T, ID> {

  protected abstract String initQuery();
  protected abstract void initStatement(T target, PreparedStatement ps) throws SQLException;


}