package com.ohs.monolithic.board;

import com.nimbusds.jose.shaded.gson.Gson;
import com.ohs.monolithic.board.controller.PostApiController;
import com.ohs.monolithic.board.dto.BulkInsertForm;
import com.ohs.monolithic.board.service.BoardManageService;
import com.ohs.monolithic.board.service.PostReadService;
import com.ohs.monolithic.board.service.PostWriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PostApiControllerTest {

    @InjectMocks
    private PostApiController target;

    @Mock
    private PostWriteService writeService;

    @Mock
    private PostReadService readService;
    @Mock
    private BoardManageService boardService;

    @Mock
    private MockMvc mockMvc;
    private Gson gson; // json 직렬화,역직렬화

    private String url;
    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .build();
        url="/api/posts/1/bulk";
    }

    private ResultActions sendRequest(BulkInsertForm form) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders
                        .post(url)
                        .content(gson.toJson(form))
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
    @Test
    public void bulkInsert_실패_form누락() throws Exception {

        // given

        // when
        ResultActions result = sendRequest(null);
        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void bulkInsert_실패_form검증실패() throws Exception {

        // given
        BulkInsertForm form = BulkInsertForm.builder()
                .title("Test Title")
                .count(null)
                .build();

        // when
        ResultActions result = sendRequest(form);

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void bulkInsert_실패_게시판아이디() throws Exception {

        /*UnnecessaryStubbingException은 Mockito에서 설정한 스텁(stub)이 테스트 중에 사용되지 않았을 때 발생하는 예외입니다.
        이 예외는 Mockito의 기본 엄격 모드(strictness)에서 발생하며, 테스트 코드에 불필요한 설정이 있음을 알려줍*/

        // given
        doReturn(false).when(boardService).isExist(intThat(argument -> argument != 1));
        lenient().doReturn(true).when(boardService).isExist(1); // lenient : 엄격 모드(strictness)를 조절

        List<Integer> tryIDs = List.of(-1,0,2);
        BulkInsertForm form = BulkInsertForm.builder()
                .title("Test Title")
                .count(100L)
                .build();


        for(Integer idx : tryIDs) {
            // when
            url = String.format( "/api/posts/%d/bulk", idx);
            ResultActions result = sendRequest(form);

            // then
            result.andExpect(status().isBadRequest());
        }
    }

    @Test
    public void bulkInsert_성공_100개() throws Exception {
        doReturn(true).when(boardService).isExist(1);
        doNothing().when(writeService).createAll(eq(1), anyList());

        BulkInsertForm form = BulkInsertForm.builder()
                .title("Test Title")
                .count(100L)
                .build();

        // when
        ResultActions result = sendRequest(form);

        // then
        result.andExpect(status().isCreated());
        verify(writeService).createAll(eq(1), anyList());
    }


}