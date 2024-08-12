package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import com.audition.configuration.PropertyResolver;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PropertyResolver propertyResolver;

    @Autowired
    AuditionLogger auditionLogger;

    static Logger log = LoggerFactory.getLogger(AuditionIntegrationClient.class);


    public List<AuditionPost> getPosts() {
        List<AuditionPost> auditionPost = new ArrayList<>();
        try {
            ResponseEntity<List<AuditionPost>> response = restTemplate.exchange(
                propertyResolver.getPostsUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AuditionPost>>() {
                });

            if (!ObjectUtils.isEmpty(response)) {
                auditionLogger.debug(log, "non null response received from downstream api");
                auditionPost = response.getBody();
            }
        } catch (final HttpClientErrorException e) {
            handleClientException(null, e);
        }
        return auditionPost;
    }

    public AuditionPost getPostById(final String id) {
        AuditionPost auditionPost = null;
        try {
            auditionPost = restTemplate.getForObject(propertyResolver.getPostByIdUrl(), AuditionPost.class,
                id);
        } catch (final HttpClientErrorException e) {
            handleClientException(id, e);
        }
        return auditionPost;

    }

    private void handleClientException(String id, HttpClientErrorException e) {
        auditionLogger.error(log, e.getLocalizedMessage());
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found",
                404);
        } else {
            throw new SystemException(e.getLocalizedMessage(), e.getStatusText(), e);
        }
    }


    public AuditionPost getComments(String postId) {

        //get post by post id
        AuditionPost post = getPostById(postId);

        //get comments for a post
        try {
            ResponseEntity<List<Comment>> response = restTemplate.exchange(propertyResolver.getPostCommentsUrl(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Comment>>() {
                }, postId);

            //add comments to the post object
            if (!ObjectUtils.isEmpty(response) && !ObjectUtils.isEmpty(post)) {
                post.setComments(response.getBody());
            }

        } catch (final HttpClientErrorException e) {
            handleClientException(null, e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return post;
    }

    public List<Comment> getCommentsByPostId(String postId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("postId", postId);
        List<Comment> comments = new ArrayList<>();
        try {
            ResponseEntity<List<Comment>> response = restTemplate.exchange(propertyResolver.getCommentsByPostIdUrl(),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Comment>>() {
                }, params);

            if (!ObjectUtils.isEmpty(response)) {
                auditionLogger.debug(log, "non null response received from downstream api");
                comments = response.getBody();
            }
        } catch (final HttpClientErrorException e) {
            handleClientException(null, e);
        }
        return comments;
    }
}
