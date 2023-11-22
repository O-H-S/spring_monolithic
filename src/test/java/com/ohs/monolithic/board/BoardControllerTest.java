package com.ohs.monolithic.board;

import com.nimbusds.jose.shaded.gson.Gson;
import com.ohs.monolithic.board.controller.BoardManageController;
import com.ohs.monolithic.board.service.BoardManageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BoardControllerTest {

    @InjectMocks
    private BoardManageController target;

    private MockMvc mockMvc;
    private Gson gson; // json 직렬화,역직렬화

    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .build();
    }
    @Test
    public void mockMvc가Null이아님() throws Exception {


        assertThat(target).isNotNull();
        assertThat(mockMvc).isNotNull();
    }
}
