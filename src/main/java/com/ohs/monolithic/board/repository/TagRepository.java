package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.PostTag;
import com.ohs.monolithic.board.domain.Tag;
import com.ohs.monolithic.common.utils.BulkInsertableRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long>, BulkInsertableRepository<Tag, Long> {
  Tag findByName(String name);

  @Query("SELECT t FROM Tag t WHERE t.name IN :names")
  List<Tag> findByNameIn(@Param("names") List<String> names);

}
