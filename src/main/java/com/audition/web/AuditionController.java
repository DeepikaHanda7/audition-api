package com.audition.web;

import com.audition.common.logging.AuditionLogger;
import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import com.audition.service.AuditionService;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuditionController {

    @Autowired
    AuditionService auditionService;

    @Autowired
    AuditionLogger auditionLogger;


    // Filter posts based on userId.
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(@NotNull @RequestParam(name = "userId") int userId) {

        auditionLogger.info(log, "Request received to get Post for userid: " + userId);
        //Get all the Posts
        List<AuditionPost> posts = auditionService.getPosts();
        List<AuditionPost> filterPost = new ArrayList<>();

        //Filter the post data based on userId
        if (!ObjectUtils.isEmpty(posts)) {
            filterPost = posts.stream()
                .filter(post -> post.getUserId() == userId).collect(Collectors.toList());
        }

        auditionLogger.info(log, "Returned response Post for userid: " + userId);
        return filterPost;

    }


    //Get post details by post id
    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPosts(@NotNull @PathVariable("id") final String postId) {

        auditionLogger.info(log, "Request received to get Post for id: " + postId);

        // TODO Add input validation - Not sure what is th ask here?
        return auditionService.getPostById(postId);
    }


    // Get comments for each post.
    @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Comment> getComments(@NotNull @RequestParam(name = "postId") final String postId) {

        auditionLogger.info(log, "Request received to get comments for id: " + postId);

        // TODO Add input validation - Not sure what is th ask here?
        return auditionService.getCommentsByPost(postId);
    }


    // Get comments for each post with Comments
    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostComments(@NotNull @PathVariable("id") final String postId) {

        auditionLogger.info(log, "Request received to get posts with all the comments for id: " + postId);

        // TODO Add input validation - Not sure what is th ask here?
        return auditionService.getPostsWithComments(postId);
    }
}
