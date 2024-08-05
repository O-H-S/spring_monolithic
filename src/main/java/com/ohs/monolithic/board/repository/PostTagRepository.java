package com.ohs.monolithic.board.repository;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostTag;
import com.ohs.monolithic.board.domain.constants.PostTagType;
import com.ohs.monolithic.common.utils.BulkInsertableRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long>, BulkInsertableRepository<PostTag, Long> {

  @Query("SELECT t.name FROM PostTag pt JOIN pt.tag t WHERE pt.post.id = :postId")
  List<String> findAllTagNames(@Param("postId") Long postId);

  @Query("SELECT pt FROM PostTag pt JOIN FETCH pt.tag WHERE pt.post.id IN :postIds")
  List<PostTag> findAllByPostIds(@Param("postIds") List<Long> postIds);

  @EntityGraph(attributePaths = {"tag"})
  List<PostTag> findAllByPost(Post post);

  @Query("SELECT pt FROM PostTag pt WHERE pt.post = :post")
  List<PostTag> findAllByPostWithoutTagLoad(@Param("post") Post post);


  // pt.tag.name 으로 바로 접근하였더니 오류 발생하여, 서브 쿼리를 통해 명시적으로 정의함.
  @Modifying
  @Query("UPDATE PostTag pt SET pt.type = :type " +
          "WHERE pt.post.id = :postId AND pt.type <> 'System'" +
          "AND pt.tag.id IN (SELECT t.id FROM Tag t WHERE t.name NOT IN :tagNames)")
  void updateTypeAllByTagNames(@Param("postId") Long postId, @Param("tagNames") List<String> tagNames, @Param("type") PostTagType type);



}
