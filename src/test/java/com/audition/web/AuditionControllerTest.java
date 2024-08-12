package com.audition.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import common.MockResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuditionControllerTest {

    @InjectMocks
    private AuditionController auditionController;

    @Mock
    private AuditionService auditionService;

    @Mock
    private AuditionLogger auditionLogger;

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
    public void getPostsReturnsResponse_200_Ok() {
        when(auditionService.getPosts()).thenReturn(auditionPostList);
        List<AuditionPost> auditionPosts = auditionController.getPosts(2);
        assertThat(auditionPosts.size()).isEqualTo(1);
        assertThat(auditionPosts.get(0).getTitle()).isEqualTo("title2");
        assertThat(auditionPosts.get(0).getId()).isEqualTo(22);
        assertThat(auditionPosts.get(0).getComments().get(2).getBody()).isEqualTo("body3");
    }


}