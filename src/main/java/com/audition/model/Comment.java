package com.audition.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private String id;
    private String postId;
    private String name;
    private String email;
    private String body;
}
