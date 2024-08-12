package com.audition.web;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.MockResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuditionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @InjectMocks
    private AuditionController auditionController;

    @MockBean
    private AuditionService auditionService;

    //    @Mock
//    private RestTemplate restTemplate;
//
//    private ResponseEntity<List<AuditionPost>> auditionResponse = null;
//    private ResponseEntity<List<Comment>> commentResponse = null;
//
//    private AuditionPost auditionPost;
    private final List<AuditionPost> auditionPostList = new ArrayList<>();
    private final List<Comment> commentList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(auditionController, "auditionService", auditionService);

        commentList.add(MockResponse.createComment("id1", "postId1", "name1", "email1", "body1"));
        commentList.add(MockResponse.createComment("id2", "postId2", "name2", "email2", "body2"));
        commentList.add(MockResponse.createComment("id3", "postId3", "name3", "email3", "body3"));

        auditionPostList.add(MockResponse.createAuditionPost(1, 11, "title1", "body1", Collections.emptyList()));
        auditionPostList.add(MockResponse.createAuditionPost(2, 22, "title2", "body2", commentList));

    }

    @Test
    public void getPostsIntReturnsResponse_200_Ok() throws Exception {
        when(auditionService.getPosts()).thenReturn(auditionPostList);

        mockMvc.perform(get("/posts")
                .contentType(new MediaType("application", "json"))
                .accept(new MediaType("application", "json"))
                .param("userId", "1"))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$[0].title", is("title1")));
    }

    @Test
    public void getPostsIntReturns_400_error_for_missing_input() throws Exception {
        when(auditionService.getPosts()).thenReturn(auditionPostList);

        mockMvc.perform(get("/posts")
                .contentType(new MediaType("application", "json"))
                .accept(new MediaType("application", "json"))
                .param("userId", ""))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.detail", is("Failed to convert 'userId' with value: ''")));
    }


}