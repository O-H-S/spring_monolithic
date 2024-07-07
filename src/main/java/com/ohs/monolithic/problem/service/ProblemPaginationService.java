package com.ohs.monolithic.problem.service;


import com.ohs.monolithic.auth.domain.AppUser;
import com.ohs.monolithic.board.domain.Board;
import com.ohs.monolithic.board.dto.PostPaginationDto;
import com.ohs.monolithic.problem.dto.ProblemPaginationDto;
import com.ohs.monolithic.problem.repository.ProblemRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemPaginationService {
  final ProblemRepository problemRepository;

  @Getter
  public static class PaginationOption{
    public enum SortColumn{
      Level,
      PostCount,
      FoundDate,
    }
    SortColumn sortColumn;
    List<String> platforms;
    String keywords;
    Boolean isDescending;
    Float[] levelRange;
    @Builder
    public PaginationOption(String keywords, List<String> platforms, String sortColumn, Boolean isDescending, Float[] levelRange) {

      try{this.sortColumn = SortColumn.valueOf(sortColumn);}
      catch(Exception e) {this.sortColumn = null;}
      this.platforms = platforms;

      StringBuilder stringBuilder = new StringBuilder();
      if(keywords != null && !keywords.isEmpty()){
        String[] refined = keywords.split(" ");

        for(String word : refined){
          //stringBuilder.append("(");
          stringBuilder.append(convertToNGrams(word));
          //stringBuilder.append(") ");
        }
        this.keywords = stringBuilder.toString();
      }

      //this.keywords = keywords;
      this.isDescending = isDescending;

      if(levelRange != null){
        if(levelRange[0] == null)
          levelRange[0] = 0f;
        if(levelRange[1] == null)
          levelRange[1] = Float.MAX_VALUE;
        if(levelRange[0] > levelRange[1])
          levelRange[1] = levelRange[0];
      }

      this.levelRange = levelRange;
    }

    String convertToNGrams(String input) {
      if(input.length() == 1)
        return input + "* ";
      StringBuilder result = new StringBuilder();
      // 문자열을 두 글자씩 분리합니다.
      for (int i = 0; i < input.length() - 1; i++) {
        if (i > 0) {
          result.append(" ");
        }
        result.append("+").append(input, i, i + 2);
      }
      return result.toString();
    }

    public String GetSortKey(){
      String sortKey = "foundDate";
      if(sortColumn != null) {
        switch (sortColumn)
        {
          case Level -> sortKey = "level";
          case PostCount -> sortKey ="postCount";
        }
      }
      return sortKey;
    }
  }

  @Transactional(readOnly = true)
  public Page<ProblemPaginationDto> getList(int page, Integer pageSize, AppUser user) {


    Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "foundDate"));
    return this.problemRepository.selectAll(pageable, user != null? user.getAccountId(): null);
  }

  @Transactional(readOnly = true)
  public Page<ProblemPaginationDto> getListWithOption(int page, Integer pageSize, PaginationOption option, AppUser user) {


    Pageable pageable = PageRequest.of(page, pageSize, Sort.by(option.isDescending ? Sort.Direction.DESC : Sort.Direction.ASC, option.GetSortKey()));

    return this.problemRepository.selectByDetails(pageable, user != null? user.getAccountId(): null, option.getKeywords(), true, option.getPlatforms(), option.getLevelRange());
  }




}
