package com.ohs.monolithic.board.controller;

import com.ohs.monolithic.board.domain.Post;
import com.ohs.monolithic.board.dto.BulkInsertForm;
import com.ohs.monolithic.board.service.BoardManageService;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.board.service.PostWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostApiController {

    final private BoardManageService boardService;
    final private PostWriteService writeService;
    final private PostReadService readService;
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/{boardId}/bulk")
    public ResponseEntity<?> bulkInsertPosts(@Valid @RequestBody BulkInsertForm form, @PathVariable("boardId") Integer boardId, BindingResult bindingResult){

        System.out.println("called");
        // 리팩토링 필요 : global exception handler로 관리하기
        if(bindingResult.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(!boardService.isExist(boardId)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // 다른 생성로직 필요해보임. 비즈니스 로직과 프레젠테이션 로직이 결합되는 문제.
        LocalDateTime nowTime = LocalDateTime.now();
        /*List<Post> posts = new ArrayList<>();

        for (int i = 0; i < form.getCount(); i++) {
            posts.add(Post.builder()
                    .title(form.getTitle())
                    .author(null)
                    .createDate(nowTime)
                    .content(String.format("(%d / %d)", i+1, form.getCount()))
                    .build()
            );
            nowTime = nowTime.plusSeconds(1);
        }*/
        Post reusablePost = Post.builder()
                .title(form.getTitle())
                .author(null)
                .createDate(nowTime)
                //.content(String.format("(%d / %d)", i+1, form.getCount()))
                .build();
        //writeService.createAll(boardId, posts);
        //writeService.createAllAsync(boardId, posts, null);
        long startTime = System.currentTimeMillis();
        writeService.createAllAsync(boardId, null, idx -> {
            reusablePost.setContent(String.format("[%d / %d] \n 까지 총 %d (ms) 만큼 소요 되었습니다. ", idx+1, form.getCount(), System.currentTimeMillis()-startTime ));
            reusablePost.setCreateDate(nowTime.plusNanos(idx*1000));
            return reusablePost;
        }, form.getCount());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
