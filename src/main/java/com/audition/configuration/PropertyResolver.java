package com.audition.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class PropertyResolver {

    public static final String BACKSLASH = "/";

    @Value("${downStream.api.endpoint.baseUrl}")
    private String baseUrl;

    @Value("${downStream.api.endpoint.get.Posts}")
    private String postsEndpoint;

    @Value("${downStream.api.endpoint.get.CommentsByPostId}")
    private String CommentsByPostIdEndpoint;

    @Value("${downStream.api.endpoint.get.PostById}")
    private String PostByIdEndpoint;

    @Value("${downStream.api.endpoint.get.postComment}")
    private String PostCommentsEndpoint;

    public String getPostsUrl() {
        return baseUrl + postsEndpoint;
    }

    public String getCommentsByPostIdUrl() {
        return baseUrl + BACKSLASH + CommentsByPostIdEndpoint;
    }

    public String getPostByIdUrl() {
        return baseUrl + BACKSLASH + PostByIdEndpoint;
    }

    public String getPostCommentsUrl() {
        return baseUrl + BACKSLASH + PostCommentsEndpoint;
    }

}
