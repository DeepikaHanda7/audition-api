package common;

import com.audition.model.AuditionPost;
import com.audition.model.Comment;
import java.util.List;

public class MockResponse {

    public static AuditionPost createAuditionPost(int userId, int id, String title, String body,
        List<Comment> commentList) {
        AuditionPost auditionPost = new AuditionPost();
        auditionPost.setUserId(userId);
        auditionPost.setId(id);
        auditionPost.setTitle(title);
        auditionPost.setBody(body);
        auditionPost.setComments(commentList);
        return auditionPost;
    }

    public static Comment createComment(String id, String postId, String name, String email, String body) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setPostId(postId);
        comment.setName(name);
        comment.setEmail(email);
        comment.setBody(body);
        return comment;
    }
}
