package com.ohs.monolithic.board.service;


import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.domain.PostTag;
import com.ohs.monolithic.board.domain.Tag;
import com.ohs.monolithic.board.domain.constants.PostTagType;
import com.ohs.monolithic.board.event.PostCreateEvent;
import com.ohs.monolithic.board.exception.InvalidPostTagNameException;
import com.ohs.monolithic.board.repository.PostRepository;
import com.ohs.monolithic.board.repository.PostTagRepository;
import com.ohs.monolithic.board.repository.TagRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.PolicyStatus;

import java.util.*;

import static com.ohs.monolithic.board.domain.QPost.post;
import static com.ohs.monolithic.board.domain.QPostTag.postTag;
import static com.ohs.monolithic.board.domain.QTag.tag;

@RequiredArgsConstructor
@Service
public class PostTagService {
  final private TagRepository tagRepository;
  final private PostTagRepository postTagRepository;
  final private PostRepository postRepository;
  final private JPAQueryFactory queryFactory;
  @Transactional
  public PostTag addPostTag(Long postId, String tagName, PostTagType tagType){
    Post post = postRepository.getReferenceById(postId);

    Tag tag =  tagRepository.findByName(tagName);
    if(tag == null) {
      tag = Tag.builder().name(tagName).build();
      tagRepository.save(tag);
    }
    PostTag postTag = PostTag.builder()
                    .post(post)
            .tag(tag)
            .type(tagType)
            .build();
    postTagRepository.save(postTag);
    return postTag;
  }

  @Transactional
  public void addPostTagList(Long postId, List<String> tags, List<PostTagType> tagTypes, Boolean disableUnusedTags){

    // List<Tag> 형태로 bulkInsert에 전달한다.
    List<Tag> newTags = new ArrayList<>(tags.size());
    for (String s : tags) {
      Tag newTag = new Tag();
      newTag.setName(s);

      newTags.add(newTag);
    }
    tagRepository.bulkInsert(newTags);

    // tagNames를 인자로, 포함되는 모든 Tag들을 다시 가져온다.
    List<Tag> tagsInDB = tagRepository.findByNameIn(tags);
    Map<String, Tag> tagsTable = new HashMap<>();
    for(Tag tag : tagsInDB){
      tagsTable.put(tag.getName(), tag);
    }

    // 가져온 모든 Tag들에 대해, PostTag(post, tags[i])를 생성하고 bulkInsert에 전달.
    Post postRef = postRepository.getReferenceById(postId);
    List<PostTag> postTags = new ArrayList<>(tags.size());
    for (int i = 0; i <tags.size(); i++) {
      PostTag newPostTag = new PostTag();
      newPostTag.setPost(postRef);
      newPostTag.setTag(tagsTable.get(tags.get(i)));
      newPostTag.setType(tagTypes.get(i));
      postTags.add(newPostTag);
    }
    postTagRepository.bulkInsert(postTags);


    // 포함되지 않는 기존 postTag들을 비활성화한다.
    if(disableUnusedTags){
      postTagRepository.updateTypeAllByTagNames(postId, tags, null);
    }
  }

  public String validatePostTagName(String name){
    int l = name.length();
    if(l<1) return "empty";
    if(l>20) return "tooLong";
    return null;
  }
  public void validatePostTagNames(Collection<String> tagNames){
    if(tagNames == null || tagNames.isEmpty())
      return;
    Map<String, String> invalids = new HashMap<>();
    for(String name : tagNames){
      String failedReason = validatePostTagName(name);
      if(failedReason != null)
        invalids.put(name, failedReason);

    }
    if(!invalids.isEmpty()){
      throw new InvalidPostTagNameException(invalids);
    }
  }

  @SafeVarargs
  public final void preprocessTagNames(List<String>... tagLists) {
    Set<String> occurred = new HashSet<>();
    for (List<String> tagList : tagLists) {
      if (tagList == null) {
        continue;
      }
      ListIterator<String> iterator = tagList.listIterator();
      while (iterator.hasNext()) {
        String refinedTag = iterator.next().trim();
        if(occurred.contains(refinedTag)) {
          iterator.remove();
          continue;
        }
        iterator.set(refinedTag);
        occurred.add(refinedTag);
      }
    }
  }




  @Transactional(readOnly = true)
  public List<String> getTagNamesFromPost(Long postId){
    return postTagRepository.findAllTagNames(postId);
  }

  public Map<Long, List<PostTag>> getPostListTags(List<Long> postIds){
    Map<Long, List<PostTag>> result = new HashMap<>();
    List<PostTag> allByPostIds = postTagRepository.findAllByPostIds(postIds);
    for (PostTag tag : allByPostIds) {
      Long postId = tag.getPost().getId();
      result.computeIfAbsent(postId, k -> new ArrayList<>()).add(tag);
    }
    return result;
  }

  @Transactional(readOnly = true)
  public List<PostTag> getPostTags(Long postId){
    return postTagRepository.findAllByPost(postRepository.getReferenceById(postId));
  }


  @Deprecated
  @Transactional(readOnly = true)
  public List<PostTag> getPostTags_Legacy(Long postId){
    return postTagRepository.findAllByPostWithoutTagLoad(postRepository.getReferenceById(postId));
  }


  @Transactional(readOnly = true)
  public BooleanBuilder getTagFilter( List<String> tagNames){
    BooleanBuilder tagFilter = new BooleanBuilder();

    // 대상 TAG가 없으면, 항상 조건 통과함
    if (tagNames == null || tagNames.isEmpty()) {
      return tagFilter;
    }

    List<Long> tagIds = queryFactory
            .select(tag.id)
            .from(tag)
            .where(tag.name.in(tagNames))
            .fetch();


    if (!tagIds.isEmpty()) {
      tagFilter.and(postTag.post.id.eq(post.id));
      tagFilter.and(postTag.tag.id.in(tagIds));
    }
    else{
      tagFilter.and(com.querydsl.core.types.dsl.Expressions.FALSE);
    }

    return tagFilter;
  }

}
