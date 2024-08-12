package com.audition.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.configuration.PropertyResolver;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import common.MockResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuditionIntegrationClientTest {

    @InjectMocks
    private AuditionIntegrationClient auditionIntegrationClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PropertyResolver propertyResolver;

    @Mock
    private AuditionLogger auditionLogger;

    private ResponseEntity<List<Comment>> commentResponse = null;

    //    private AuditionPost auditionPost;
    private final List<AuditionPost> auditionPostList = new ArrayList<>();
    private final List<Comment> commentList = new ArrayList<>();

    @BeforeEach
    void setUp() {

        when(propertyResolver.getPostByIdUrl()).thenReturn("testUrl");
        when(propertyResolver.getPostsUrl()).thenReturn("testUrl");
        when(propertyResolver.getPostCommentsUrl()).thenReturn("testUrl");
        when(propertyResolver.getCommentsByPostIdUrl()).thenReturn("testUrl");

        commentList.add(MockResponse.createComment("id1", "postId1", "name1", "email1", "body1"));
        commentList.add(MockResponse.createComment("id2", "postId2", "name2", "email2", "body2"));
        commentList.add(MockResponse.createComment("id3", "postId3", "name3", "email3", "body3"));

        auditionPostList.add(MockResponse.createAuditionPost(1, 11, "title1", "body1", Collections.emptyList()));
        auditionPostList.add(MockResponse.createAuditionPost(2, 22, "title2", "body2", commentList));

    }


    //getPosts
    @Test
    public void getPostsReturnsResponse_200_Ok() {
        ResponseEntity<List<AuditionPost>> auditionResponse = new ResponseEntity<>(auditionPostList,
            HttpStatusCode.valueOf(200));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), isNull(),
            Mockito.<ParameterizedTypeReference<List<AuditionPost>>>any())).thenReturn(auditionResponse);
        List<AuditionPost> auditionPosts = auditionIntegrationClient.getPosts();
        assertThat(auditionPosts.size()).isEqualTo(2);
        assertThat(auditionPosts.get(0).getTitle()).isEqualTo("title1");
        assertThat(auditionPosts.get(1).getTitle()).isEqualTo("title2");
        assertThat(auditionPosts.get(1).getComments().get(2).getBody()).isEqualTo("body3");
    }


    //getPostId
    @Test
    public void getPostByIdReturnsResponse_200_Ok() {
        when(restTemplate.getForObject("testUrl", AuditionPost.class, "11")).thenReturn(
            auditionPostList.get(0));

        AuditionPost auditionPost = auditionIntegrationClient.getPostById("11");

        assertThat(auditionPost.getUserId()).isEqualTo(1);
        assertThat(auditionPost.getId()).isEqualTo(11);
        assertThat(auditionPost.getTitle()).isEqualTo("title1");
        assertThat(auditionPost.getBody()).isEqualTo("body1");
        verify(restTemplate, times(1)).getForObject("testUrl", AuditionPost.class, "11");
    }

    @Test
    public void getPostByIdReturnsResponse_NotFound_exception() {
        when(restTemplate.getForObject("testUrl", AuditionPost.class, "11")).thenThrow(
            new HttpClientErrorException(HttpStatusCode.valueOf(404), "Not Found"));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById("11"));
        assertThat(exception.getMessage()).isEqualTo("Cannot find a Post with id 11");
        assertThat(exception.getStatusCode()).isEqualTo(404);
        verify(restTemplate, times(1)).getForObject("testUrl", AuditionPost.class, "11");
    }

    @Test
    public void getPostByIdReturnsResponse_generic_exception() {
        when(restTemplate.getForObject("testUrl", AuditionPost.class, "11")).thenThrow(
            new HttpClientErrorException(HttpStatusCode.valueOf(505), "Server not available"));

        SystemException exception = assertThrows(SystemException.class,
            () -> auditionIntegrationClient.getPostById("11"));
        assertThat(exception.getMessage()).isEqualTo("505 Server not available");
        assertThat(exception.getStatusCode()).isEqualTo(500);
        verify(restTemplate, times(1)).getForObject("testUrl", AuditionPost.class, "11");
    }


    //getComments
    @Test
    public void getCommentsReturnsResponse_200_Ok() {
        when(restTemplate.getForObject("testUrl", AuditionPost.class, "22")).thenReturn(
            auditionPostList.get(1));

        commentResponse = new ResponseEntity<>(commentList, HttpStatusCode.valueOf(200));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), isNull(),
            Mockito.<ParameterizedTypeReference<List<Comment>>>any(), anyString())).thenReturn(commentResponse);
        AuditionPost auditionPost = auditionIntegrationClient.getComments("22");
        assertThat(auditionPost.getUserId()).isEqualTo(2);
        assertThat(auditionPost.getId()).isEqualTo(22);
        assertThat(auditionPost.getTitle()).isEqualTo("title2");
        assertThat(auditionPost.getBody()).isEqualTo("body2");
        verify(restTemplate, times(1)).getForObject("testUrl", AuditionPost.class, "22");

    }


    //getCommentsByPostId
    @Test
    public void getCommentsByPostIdReturnsResponse_200_Ok() {
        commentResponse = new ResponseEntity<>(commentList, HttpStatusCode.valueOf(200));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), isNull(),
            Mockito.<ParameterizedTypeReference<List<Comment>>>any(), anyMap())).thenReturn(
            commentResponse);

        List<Comment> comments = auditionIntegrationClient.getCommentsByPostId("22");

        assertThat(comments.get(0).getId()).isEqualTo("id1");
        assertThat(comments.get(0).getBody()).isEqualTo("body1");
        assertThat(comments.get(1).getId()).isEqualTo("id2");
        assertThat(comments.get(1).getEmail()).isEqualTo("email2");
        assertThat(comments.get(2).getId()).isEqualTo("id3");
        assertThat(comments.get(2).getPostId()).isEqualTo("postId3");
        assertThat(comments.get(2).getName()).isEqualTo("name3");

    }

}