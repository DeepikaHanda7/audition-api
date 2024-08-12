package com.audition.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditionPost {

    private int userId;
    private int id;
    private String title;
    private String body;

    public List<Comment> getComments() {
        if (comments == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(comments);

    }

    private List<Comment> comments;


    public void setComments(List<Comment> comments) {
        this.comments = comments != null ? comments : new ArrayList<Comment>();
    }

}
