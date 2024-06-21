package com.ohs.monolithic.board.dto;

import com.ohs.monolithic.board.domain.constants.PostTagType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
public class PostForm {

    @NotEmpty(message="제목은 필수항목입니다.")
    @Size(max=200)
    private String subject;

    @NotEmpty(message="내용은 필수항목입니다.")
    private String content;

    private String method;
    private List<String> normalTags;
    private List<String>  highlightTags;


    public Pair<List<String>, List<PostTagType>> flatTags(){
        if(normalTags != null || highlightTags != null) {
            List<String> tagNames = Stream.of(normalTags, highlightTags)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .toList();
            List<PostTagType> tagTypes = Stream.concat(
                    Stream.ofNullable(normalTags).flatMap(List::stream).map(tag -> PostTagType.Normal),
                    Stream.ofNullable(highlightTags).flatMap(List::stream).map(tag -> PostTagType.Highlight)
            ).toList();
            return Pair.of(tagNames, tagTypes);
        }
        return null;
    }


}